package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import java.util.List;

public class ItemFaker {

    private Faker faker;

    public ItemFaker(Faker faker) {
        this.faker = faker;
    }

    public Item create(User owner) {
        Item item = new Item(owner);
        item.setName(faker.space().nasaSpaceCraft());
        item.setDescription(faker.lorem().paragraph(1));
        item.setBail(faker.number().numberBetween(20, 999));
        item.setPrice(faker.number().numberBetween(5, 50));
        item.setLocation(faker.address().cityName());
        item.setDeleted(false);
        owner.getItems().add(item);

        return item;
    }

    public void createItems(List<Item> items, User owner, int count) {
        for (int i = 0; i < count; i++) {
            items.add(create(owner));
        }
    }
}