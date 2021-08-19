package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // 변경감지 기능 사용 예시
    // 병합기능(merge)의 단점은 엔티티의 모든 속성이 변경되기 때문에 null이 잘못 들어 갈 수가 있다는 것
    // 실무는 복잡하기 때문에 가급적이면 머지를 쓰지 않고, 조금 귀찮더라도 변경감지 기능을 이용하는 것이 좋다.
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // findItem은 영속 상태이다. 따라서. @Transactional에 의해서 commit이 되고 JPA에서 flush가 되서, 변경감지가 일어나서 바뀐 값을 업데이트 쿼리를 날려서 업데이트가 된다.
        Item findItem = itemRepository.findOne(itemId);

        // 단발성으로 업데이트(setter)를 하면안되고 의미있는 method를 만들어서 사용하는 것이 좋다. -> 역추적이 좋아. 유지보수 편하다
        // findItem.change(price, name, stockQuantity)

        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }

    // 병합 기능 사용 예시
    @Transactional
    public Item updateItem2(Long itemId, Book param) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
        return findItem;
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemid) {
        return itemRepository.findOne(itemid);
    }
}
