package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository {

    @Autowired
    private final EntityManager em;

    public ItemRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item); //JPA로 등록하기 전에는  Id가 null이니까
        } else { // null이 아니라는거는 이미 db에 등록이 되었었던 객체라는 뜻
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    } //리스트로 조회할 때는 jpql을 작성해주어야 한다. 그냥 그렇대,...
}
