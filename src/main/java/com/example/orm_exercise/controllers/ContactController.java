package com.example.orm_exercise.controllers;

import com.example.orm_exercise.models.Address;
import com.example.orm_exercise.models.Contact;
import com.example.orm_exercise.repositories.ContactRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable int id) {
        return contactRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(updatedContact.getName());
            contact.setEmail(updatedContact.getEmail());
            contact.setPhoneNumber(updatedContact.getPhoneNumber());
            return contactRepository.save(contact);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable int id) {
        contactRepository.deleteById(id);
    }

    @PostMapping("/{contactId}/addresses")
    @Transactional
    public Address addAddressToContact(@PathVariable int contactId, @RequestBody Address address) {
        return contactRepository.findById(contactId).map(contact -> {
            contact.addAddress(address);
            contactRepository.save(contact);
            return address;
        }).orElse(null);
    }
    @GetMapping("/{contactId}/addresses")
    @Transactional
    public List<Address> getAddresses(@PathVariable int contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Contact not found"));

        return List.copyOf(contact.getAddresses());
    }

}
