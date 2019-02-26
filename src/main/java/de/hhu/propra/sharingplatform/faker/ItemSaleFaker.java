package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.ItemSale;
import de.hhu.propra.sharingplatform.model.User;
import java.util.List;

public class ItemSaleFaker {

    private Faker faker;

    public ItemSaleFaker(Faker faker) {
        this.faker = faker;
    }

    public ItemSale create(User owner) {
        ItemSale itemSale = new ItemSale(owner);
        itemSale.setName(faker.space().nasaSpaceCraft());
        itemSale.setDescription(faker.lorem().paragraph(1));
        itemSale.setPrice(faker.number().numberBetween(10, 523));
        itemSale.setLocation(faker.address().cityName());
        itemSale.setDeleted(false);
        owner.getItemSales().add(itemSale);

        return itemSale;
    }

    public void createItems(List<ItemSale> itemSales, User owner, int count) {
        for (int i = 0; i < count; i++) {
            itemSales.add(create(owner));
        }
    }
}