package ua.artcode.taxi.service;

import ua.artcode.taxi.exception.InputDataWrongException;
import ua.artcode.taxi.exception.OrderNotFoundException;
import ua.artcode.taxi.exception.WrongStatusOrderException;
import ua.artcode.taxi.model.Order;
import ua.artcode.taxi.model.OrderStatus;
import ua.artcode.taxi.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {


    void saveNewUser(User user);
    void saveNewOrder(Order order, User user) throws InputDataWrongException;

    User getByUserphone(String userphone);
    User getById(Long id);
    //User updateUser(Long id, User newUser);

    User updateCurrentAddressOfUser(User user) throws IOException, InputDataWrongException;

    Order getLastOrder(String userphone);
    Order getOrderById(Long id);
    //Order updateOrder(Long id, Order newOrder);
    Order calculateOrder(Order baseOrder) throws InputDataWrongException;

    List<Order> getListOrdersOfUser(String userphone);
    List<Order> getListOrdersByOrderStatus(OrderStatus orderStatus);

    Map<Long, Double> createMapOrdersIdDistancesKmToUser(List<Order> orders)
            throws InputDataWrongException, IOException;
    //List<User> getListUsersFromDistancesToUser(int maxDistanceToUserInKm, User currentUser) throws InputDataWrongException, IOException;
    //List<User> getListUserFromUserIds(Collection<Long> userIds);
    Map<Long, User> getMapUsersFromUserOrders(List<Order> orders, boolean passenger);

    Order takeOrderByDriver(Long orderId, User driver)
            throws OrderNotFoundException, WrongStatusOrderException;
    Order cancelOrder(Long orderId, User user) throws OrderNotFoundException, WrongStatusOrderException;
    Order closeOrder(Long orderId, User user) throws OrderNotFoundException, WrongStatusOrderException;
}
