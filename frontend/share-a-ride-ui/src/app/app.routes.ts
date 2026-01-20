import { Routes } from '@angular/router';
import { AppLayoutComponent } from './layout/app-layout/app-layout';

export const routes: Routes = [
    {
        path: '',
        component: AppLayoutComponent,
        children: [
            {
                path: '',
                loadComponent: () =>
                    import('./pages/landing/landing').then((m) => m.LandingComponent),
            },
            {
                path: 'find',
                loadComponent: () =>
                    import('./pages/find-ride/find-ride').then((m) => m.FindRideComponent),
            },
            {
                path: 'post-ride',
                loadComponent: () =>
                    import('./pages/post-ride/post-ride').then((m) => m.PostRideComponent),
            },
            {
                path: 'my-rides',
                loadComponent: () =>
                    import('./pages/my-rides/my-rides').then((m) => m.MyRidesComponent),
            },
            {
                path: 'booking/:id',
                loadComponent: () =>
                    import('./pages/booking-confirmation/booking-confirmation').then((m) => m.BookingConfirmationComponent),
            },
            {
                path: 'booking/:id/cancel',
                loadComponent: () =>
                    import('./pages/booking-confirmation/booking-confirmation').then((m) => m.BookingConfirmationComponent),
            },
            {
                path: 'payment',
                loadComponent: () =>
                    import('./pages/payment/payment').then((m) => m.PaymentComponent),
            },
            {
                path: 'chat/:rideId',
                loadComponent: () =>
                    import('./pages/chat/chat').then((m) => m.ChatComponent),
            },
            {
                path: 'feedback/:rideId',
                loadComponent: () =>
                    import('./pages/feedback/feedback').then((m) => m.FeedbackComponent),
            },
            {
                path: 'driver/:driverId',
                loadComponent: () =>
                    import('./pages/driver-profile/driver-profile').then((m) => m.DriverProfileComponent),
            },
            {
                path: 'about',
                loadComponent: () =>
                    import('./pages/about-us/about-us').then((m) => m.AboutUsComponent),
            },
            {
                path: 'contact',
                loadComponent: () =>
                    import('./pages/contact-us/contact-us').then((m) => m.ContactUsComponent),
            },
            {
                path: 'reviews',
                loadComponent: () =>
                    import('./pages/reviews/reviews').then((m) => m.ReviewsComponent),
            },
            {
                path: 'imprint',
                loadComponent: () =>
                    import('./pages/imprint/imprint').then((m) => m.ImprintComponent),
            },
            {
                path: 'privacy',
                loadComponent: () =>
                    import('./pages/privacy-policy/privacy-policy').then((m) => m.PrivacyPolicyComponent),
            },
            {
                path: 'support',
                loadComponent: () =>
                    import('./pages/support/support').then((m) => m.SupportComponent),
            },
        ],
    },
    { path: '**', redirectTo: '' },
];