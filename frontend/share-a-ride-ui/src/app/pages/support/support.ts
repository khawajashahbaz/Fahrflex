import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PersonRefDto, PersonsApi } from '../../core/api/person-api';
import { RideHistoryDto, RidesApi } from '../../core/api/rides-api';

type ProblemType = 'PAYMENT' | 'NO_SHOW' | 'AGGRESSION';

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './support.html',
  styleUrl: './support.scss',
})
export class SupportComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PersonsApi);
  private readonly rideApi = inject(RidesApi);
  private readonly cdr = inject(ChangeDetectorRef);

  rides: RideHistoryDto[] = [];
  participants: PersonRefDto[] = [];
  // Prototype: treat this as the logged-in user
  private readonly currentPersonId = 'person_passenger_1';

  loadingUser = false;
  uiError = '';

  form = this.fb.group({
    firstname: ['', [Validators.required]],
    lastname: [''], // optional for prototype (same rationale as Contact)
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required]],

    rideId: [''],        // optional
    passengerId: [''],   // optional, shown only if rideId filled

    problem: ['PAYMENT' as ProblemType, [Validators.required]],
    message: ['', [Validators.required, Validators.minLength(10)]],
  });

  ngOnInit(): void {
    this.loadingUser = true;

    this.api.getPersonForContact(this.currentPersonId).subscribe({
      next: (p) => {
        this.form.patchValue({
          firstname: p.forename ?? '',
          lastname: p.lastname ?? '',
          email: p.email ?? '',
          phone: p.phoneNumber ?? '',
        });
        this.loadingUser = false;

        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingUser = false;
        this.uiError = 'Could not prefill your profile data. Please fill it in manually.';
        this.cdr.markForCheck();
      },
    });

    this.rideApi.getPersonRides(this.currentPersonId).subscribe({
      next: (rides) => {
        this.rides = rides;
        this.cdr.markForCheck();
      },
      error: () => {
        // not fatal
      }
    });
    // If rideId gets cleared, also clear passengerId to keep the form consistent
    this.form.get('rideId')?.valueChanges.subscribe((v) => {
      const rideId = (v ?? '').toString().trim();

      this.participants = [];
      this.form.patchValue({ passengerId: '' }, { emitEvent: false });

      if (!rideId) {
        this.cdr.markForCheck();
        return;
      }

      this.api.getRideParticipants(rideId).subscribe({
        next: (people) => {
          // exclude the current user from selectable "other person"
          this.participants = people.filter(p => p.personId !== this.currentPersonId);
          this.cdr.markForCheck();
        },
        error: () => {
          this.participants = [];
          this.cdr.markForCheck();
        }
      });
    });
  }

  showPassengerId(): boolean {
    const rideId = (this.form.value.rideId ?? '').trim();
    return rideId.length > 0;
  }

  canOpenMail(): boolean {
    const v = this.form.value;
    const emailOk = !!(v.email ?? '').trim() && this.form.get('email')?.valid;
    const firstnameOk = !!(v.firstname ?? '').trim();
    const phoneOk = !!(v.phone ?? '').trim();
    const problemOk = !!(v.problem ?? '').trim();
    const messageOk = !!(v.message ?? '').trim() && (v.message ?? '').trim().length >= 10;

    return Boolean(emailOk && firstnameOk && phoneOk && problemOk && messageOk);
  }

  openMailDraft(): void {
    this.uiError = '';
    if (!this.canOpenMail()) return;

    try {
      window.location.href = this.buildMailtoHref();
    } catch {
      this.uiError = 'Could not create the email draft. Please try again.';
    }
  }

  private buildMailtoHref(): string {
    const v = this.form.value;

    const firstname = (v.firstname ?? '').trim();
    const lastname = (v.lastname ?? '').trim();
    const email = (v.email ?? '').trim();
    const phone = (v.phone ?? '').trim();

    const rideId = (v.rideId ?? '').trim();
    const passengerId = (v.passengerId ?? '').trim();

    const problem = (v.problem ?? 'PAYMENT').trim();
    const message = (v.message ?? '').trim();

    const to = 'fahrflex-support@example.com';

    const subject = `FahrFlex Support – ${this.problemLabel(problem as ProblemType)}`;

    const other = this.getSelectedPersonInfo();

    const headerLine =
      `RideId: ${rideId || '-'}   |    ` +
      (rideId
        ? (other
          ? `Person: ${other.name} (${other.role})   |    PersonId: ${other.id}`
          : `Person: -   |    PersonId: -`)
        : `Person: -   |    PersonId: -`);

    const body =
      `${headerLine}\n\n` +
      `Topic/Problem: ${this.problemLabel(problem as ProblemType)}\n\n` +
      `${message}\n\n` +
      `Best regards\n` +
      `${firstname} ${lastname}`.trim() +
      `\n\n` +
      `Contact me: ${email}  |    ${phone}`;

    const qs = `subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    return `mailto:${to}?${qs}`;
  }

  private problemLabel(p: ProblemType): string {
    switch (p) {
      case 'PAYMENT':
        return 'Payment';
      case 'NO_SHOW':
        return 'No-show';
      case 'AGGRESSION':
        return 'Aggression / Unfriendly';
      default:
        return p;
    }
  }

  private getSelectedPersonInfo(): { id: string; name: string; role: string } | null {
    const selectedId = (this.form.value.passengerId ?? '').trim(); // field name can stay
    if (!selectedId) return null;

    const p = this.participants.find(x => x.personId === selectedId);
    if (!p) return { id: selectedId, name: selectedId, role: 'PERSON' };

    return {
      id: p.personId,
      name: (p.name ?? p.personId),
      role: p.role,
    };
  }
}