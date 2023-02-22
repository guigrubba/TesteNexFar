package br.com.nexfar.teste.teste_bakcend_nexfar.repository;

import br.com.nexfar.teste.teste_bakcend_nexfar.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, Long> {



    @Query("?0")
    List<Order> findDadesOrder(String query);
}
