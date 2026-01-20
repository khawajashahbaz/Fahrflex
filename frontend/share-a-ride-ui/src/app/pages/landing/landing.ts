import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
})
export class LandingComponent {
  departureCity = '';
  destinationCity = '';
  date = ''; // yyyy-mm-dd
  time = ''; // HH:mm

  // default filters (optional)
  minSeats = 1;
  minLuggage = 0;

  constructor(private router: Router) {}

  goToFind(): void {
    const dep = this.departureCity.trim();
    const dst = this.destinationCity.trim();

    // If empty, still allow navigation, but /find will not search automatically
    this.router.navigate(['/find'], {
      queryParams: {
        departureCity: dep || null,
        destinationCity: dst || null,
        date: this.date || null,
        time: this.time || null,
        minSeats: this.minSeats,
        minLuggage: this.minLuggage,
      },
    });
  }
}