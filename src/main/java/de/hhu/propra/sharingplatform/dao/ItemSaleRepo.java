package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.items.ItemSale;

import java.util.List;

public interface ItemSaleRepo extends ItemRepo<ItemSale> {

    List<ItemSale> findAllByDeletedIsFalse();
}
