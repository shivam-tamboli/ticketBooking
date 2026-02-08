package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Train;
import org.example.entity.User;
import org.example.util.UserServiceUtil;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBookingService {

    private User user;

    private List<User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_FILE_PATH = "src/main/resources/localDb/users.json";

    public UserBookingService(User user) throws IOException {

        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    public void loadUserListFromFile() throws IOException{
        userList = objectMapper.readValue(new File(USERS_FILE_PATH), new TypeReference<List<User>>() {});
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try {
            userList.add((user1));
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException{
        File userFile = new File(USERS_FILE_PATH);
        objectMapper.writeValue(userFile, userFile);
    }

    public void fetchBooking(){
        user.printTickets();
    }

    public Boolean cancelTicket(String ticketId){
        if(ticketId == null || ticketId.isEmpty()){
            return Boolean.FALSE;
        }
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));
        return removed;
    }

    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch (IOException ex){
            return new ArrayList<>();
        }
    }

}
