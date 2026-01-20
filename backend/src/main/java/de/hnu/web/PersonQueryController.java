package de.hnu.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Person;
import de.hnu.repo.PersonRepository;
import de.hnu.web.dto.PersonContactDto;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonQueryController {

    private final PersonRepository personRepo;

    public PersonQueryController(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    @GetMapping("/{id}")
    public PersonContactDto getPersonForContact(@PathVariable Integer id) {
        Person p = personRepo.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found: " + id));

        PersonContactDto dto = new PersonContactDto();
        dto.id = p.getId();
        dto.name = p.getName();
        dto.email = p.getEmail();
        dto.phoneNumber = p.getPhoneNumber();

        // Derive forename/lastname from "name" (simple, good enough for prototype)
        if (p.getName() != null) {
            String trimmed = p.getName().trim();
            int idx = trimmed.indexOf(' ');
            if (idx > 0) {
                dto.forename = trimmed.substring(0, idx).trim();
                dto.lastname = trimmed.substring(idx + 1).trim();
            } else {
                dto.forename = trimmed;
                dto.lastname = "";
            }
        }
        return dto;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class PersonNotFoundException extends RuntimeException {
        PersonNotFoundException(String msg) { super(msg); }
    }
}