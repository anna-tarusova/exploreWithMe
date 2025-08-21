package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @JsonProperty("lat")
    @Min(-90)
    @Max(90)
    @NotNull
    Double locationLat;
    @JsonProperty("lon")
    @NotNull
    @Min(-180)
    @Max(180)
    Double locationLon;
}
