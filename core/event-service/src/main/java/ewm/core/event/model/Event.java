package ewm.core.event.model;

import ewm.core.category.model.Category;
import ewm.core.compilation.model.Compilation;
import ewm.core.dto.EventState;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    private Boolean paid;
    private Integer participantLimit;

    @Column(name = "participant_confirmed")
    @ToString.Exclude
    private Integer participantConfirmed;

    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;
    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private Long initiatorId;

    @Embedded
    private Location location;

    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations;
}