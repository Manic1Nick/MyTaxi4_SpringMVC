package ua.artcode.taxi.service;

import ua.artcode.taxi.exception.*;
import ua.artcode.taxi.model.Address;
import ua.artcode.taxi.model.Order;
import ua.artcode.taxi.model.OrderStatus;
import ua.artcode.taxi.model.User;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public interface UserService {


    void save(User user);
    void save(Order order, User user) throws InputDataWrongException;

    User findByUsername(String username);
    User findByUserphone(String userphone);

    User findById(Long id);
    User updateUser(User oldUser, User newUser);

    Order getLastOrder(String userphone);
    Order getOrderById(Long id);


    //register
    User registerPassenger(Map<String, String> map) throws RegisterException, InputDataWrongException;
    User registerDriver(Map<String, String> map) throws RegisterException, InputDataWrongException;

    //login (return accessToken)
    String login(String phone, String pass) throws LoginException;

    //actions for passenger
    Order makeOrder(String accessToken, String lineFrom, String lineTo, String message) throws OrderMakeException,
                                                UserNotFoundException, InputDataWrongException, UnknownHostException;
    Order makeOrderAnonymous(String phone, String name, String from, String to, String message) throws
                                            OrderMakeException, InputDataWrongException, UnknownHostException;
    Map<String, Object> calculateOrder(String lineFrom, String lineTo) throws
                                            InputDataWrongException, UnknownHostException;
    Order getOrderInfo(Long orderId) throws OrderNotFoundException;
    Order getLastOrderInfo(String accessToken) throws UserNotFoundException, OrderNotFoundException;
    Order cancelOrder(Long orderId) throws OrderNotFoundException, WrongStatusOrderException;
    Order closeOrder(String accessToken, Long orderId) throws OrderNotFoundException,
                                    WrongStatusOrderException, DriverOrderActionException;

    //actions for driver
    Order takeOrder(String accessToken, Long orderId)
            throws OrderNotFoundException, WrongStatusOrderException, DriverOrderActionException;
    Order[] createArrayOrdersForDriver(OrderStatus orderStatus, User driver)
            throws InputDataWrongException;
    Map<Integer, Order> getMapDistancesToDriver(String orderStatus, String lineAddressDriver)
            throws InputDataWrongException;

    //actions for all
    User getUser(String accessToken);
    User updateUser(Map<String, String> map, String accessToken) throws RegisterException;
    User deleteUser(String accessToken) throws WrongStatusOrderException;
    Address getUserLocation();
    Order updateOrder(Order order);
    List<Order> getOrdersOfUser (Long userId, int from, int to);
    int getQuantityOrdersOfUser (Long userId);
}
