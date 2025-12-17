package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.Organizer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerResponse {
    private Long id;
    private String name;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private Long userId;

    public OrganizerResponse(Organizer organizer) {
        this.id = organizer.getId();
        this.name = organizer.getName();
        this.description = organizer.getDescription();
        this.contactEmail = organizer.getContactEmail();
        this.contactPhone = organizer.getContactPhone();
        this.userId = organizer.getUserId();
    }
}
