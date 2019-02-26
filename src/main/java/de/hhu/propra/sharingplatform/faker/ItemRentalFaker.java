package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import java.util.List;

public class ItemRentalFaker {

    private Faker faker;

    public ItemRentalFaker(Faker faker) {
        this.faker = faker;
    }

    public ItemRental create(User owner) {
        ItemRental itemRental = new ItemRental(owner);
        itemRental.setName(faker.space().nasaSpaceCraft());
        itemRental.setDescription(faker.lorem().paragraph(1));
        itemRental.setBail(faker.number().numberBetween(20, 999));
        itemRental.setDailyRate(faker.number().numberBetween(5, 50));
        itemRental.setLocation(faker.address().cityName());
        itemRental.setDeleted(false);
        owner.getItemRentals().add(itemRental);

        return itemRental;
    }

    public void createItems(List<ItemRental> itemRentals, User owner, int count) {
        for (int i = 0; i < count; i++) {
            itemRentals.add(create(owner));
        }
    }
}