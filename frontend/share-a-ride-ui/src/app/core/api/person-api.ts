import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface PersonContactDto {
    id: string;
    name?: string;
    email?: string;
    phoneNumber?: string;
    forename?: string;
    lastname?: string;
}

export interface PersonRefDto {
    personId: string;
    name?: string;
    role: 'DRIVER' | 'PASSENGER';
}

@Injectable({ providedIn: 'root' })
export class PersonsApi {
    private readonly baseUrl = 'http://localhost:8080/api';

    constructor(private http: HttpClient) { }

    getPersonForContact(id: string) {
        return this.http.get<PersonContactDto>(`${this.baseUrl}/persons/${id}`);
    }

    getRideParticipants(rideId: string) {
        return this.http.get<PersonRefDto[]>(`${this.baseUrl}/rides/${rideId}/participants`);
    }
}