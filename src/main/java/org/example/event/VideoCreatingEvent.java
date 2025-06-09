package org.example.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCreatingEvent {
    private UUID authorId;
    private String title;
    private String description;
    private String videoUrl;
}
