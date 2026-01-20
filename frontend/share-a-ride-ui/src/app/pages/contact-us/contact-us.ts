import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PersonsApi } from '../../core/api/person-api';

@Component({
  selector: 'app-contact-us',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './contact-us.html',
  styleUrl: './contact-us.scss',
})
export class ContactUsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(PersonsApi);
  private readonly cdr = inject(ChangeDetectorRef);

  // Prototype: "logged in user"
  private readonly currentPersonId = 'person_passenger_1';

  uiError = '';
  loadingUser = false;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    forename: ['', [Validators.required]],
    lastname: [''],
    message: ['', [Validators.required, Validators.minLength(10)]],
  });

  ngOnInit(): void {
    this.loadingUser = true;
    this.api.getPersonForContact(this.currentPersonId).subscribe({
      next: (p) => {
        this.form.patchValue({
          email: p.email ?? '',
          forename: p.forename ?? '',
          lastname: p.lastname ?? '',
        });
        this.loadingUser = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingUser = false;
        // Keep the form editable even if prefill fails
        this.uiError = 'Could not prefill your profile data. Please fill it in manually.';
        this.cdr.markForCheck();
      },
    });
  }

  canOpenMail(): boolean {
    const v = this.form.value;
    const emailOk = !!(v.email ?? '').trim() && this.form.get('email')?.valid;
    const forenameOk = !!(v.forename ?? '').trim();
    const messageOk = !!(v.message ?? '').trim() && (v.message ?? '').trim().length >= 10;
    return Boolean(emailOk && forenameOk && messageOk);
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

    const email = (v.email ?? '').trim();
    const forename = (v.forename ?? '').trim();
    const lastname = (v.lastname ?? '').trim();
    const message = (v.message ?? '').trim();

    const to = 'fahrflex-support@example.com';
    const subject = `FahrFlex – Contact request from ${forename} ${lastname}`.trim();

    const body =
      `${message}\n\n` +
      `Best regards\n` +
      `${forename} ${lastname}\n` +
      `Contact me: ${email}`;

    const qs = `subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    return `mailto:${to}?${qs}`;
  }
}