package de.hhu.propra.sharingplatform.model.items;

import com.google.common.io.Files;
import de.hhu.propra.sharingplatform.model.User;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@Entity
@ToString(exclude = "owner")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String imageFileName;

    @Column(length = 2000)
    private String description;
    private String location;
    private boolean deleted;

    Item() {
        // this is for jpa
    }

    public Item(User owner) {
        this.owner = owner;
    }

    @Transient
    private MultipartFile image;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private User owner;

    public String getImageExtension() {
        return Files.getFileExtension(image.getOriginalFilename());
    }
}
