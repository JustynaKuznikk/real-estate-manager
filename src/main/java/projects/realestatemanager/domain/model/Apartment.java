package projects.realestatemanager.domain.model;


import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "apartments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "building")
@EqualsAndHashCode(of = "id") //todo ewentualnie do zmiany
public class Apartment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer floor;

    @Column(name = "on_corner")
    private Boolean onCorner;

    @Column(nullable = false)
    private Integer area;

    @Column(nullable = false, name = "rooms_number")
    private Integer roomsNumber;

    @Column(nullable = false, name = "type_of_kitchen")
    private String typeOfKitchen;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "price_per_square_meter")
    private Integer pricePerSquareMeter;

    //stan deweloperski/po wykończeniu/stary remont (enum???)
    @Column(nullable = false, name = "market_type")
    private String marketType;

    @Column(nullable = false)
    private String status;

    private Boolean exclusivity;
    //plan i zdjęcia TODO

    @Column(nullable = false, name = "windows_direction")
    private String windowsDirection;

    @Column(nullable = false)
    private String view;

    @Column(name = "storage_room")
    private Boolean storageRoom;

    private String comment;

    private Boolean active;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false, name = "photos_url")
    private String photosUrl;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "building_id")
    private Building building;

    @ManyToMany(mappedBy = "apartments")
    private Set<User> users = new HashSet<>();

}


