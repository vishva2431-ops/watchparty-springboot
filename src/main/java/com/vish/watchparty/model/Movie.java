package com.vish.watchparty.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    private String id;

    private String groupTitle;     // Stranger Things
    private String partTitle;      // Stranger Things 1
    private Integer partNumber;    // 1
    private String description;
    private String posterUrl;
    private String videoUrl;
}