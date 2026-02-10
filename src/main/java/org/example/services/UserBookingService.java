package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Ticket;
import org.example.entity.Train;
import org.example.entity.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class UserBookingService {

    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_FILE_PATH = "src/main/java/org/example/localDb/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        File file = new File(USERS_FILE_PATH);
        if (!file.exists()) {
            userList = new ArrayList<>();
            saveUserListToFile();
        } else {
            userList = objectMapper.readValue(file, new TypeReference<List<User>>() {});
        }
    }

    private void saveUserListToFile() throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(USERS_FILE_PATH), userList);
    }



    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream()
                .filter(u -> u.getName().equals(user.getName())
                        && UserServiceUtil.checkPassword(user.getPassword(), u.getHashedPassword()))
                .findFirst();

        if (foundUser.isPresent()) {

            this.user = foundUser.get();

            if (this.user.getTicketsBooked() == null) {
                this.user.setTicketsBooked(new ArrayList<>());
            }
            return true;
        }
        return false;
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }



    public void fetchBookings() {
        if (user == null) {
            System.out.println("Please login first.");
            return;
        }
        user.printTickets();
    }

    public List<Train> getTrains(String source, String destination) {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train) {
        if (train == null || train.getSeats() == null) {
            return new ArrayList<>();
        }
        return train.getSeats();
    }



    public Boolean bookTrainSeat(Train train, int row, int seat) {

        try {
            if (user == null) {
                System.out.println("Please login first.");
                return false;
            }

            if (train == null || train.getSeats() == null) {
                System.out.println("Invalid train data.");
                return false;
            }

            List<List<Integer>> seats = train.getSeats();

            if (row < 0 || row >= seats.size() || seat < 0 || seat >= seats.get(row).size()) {
                System.out.println("Invalid seat position.");
                return false;
            }

            if (seats.get(row).get(seat) == 1) {
                System.out.println("Seat already booked.");
                return false;
            }


            seats.get(row).set(seat, 1);
            train.setSeats(seats);

            TrainService trainService = new TrainService();
            trainService.updateTrain(train);


            Ticket ticket = new Ticket(
                    UUID.randomUUID().toString(),
                    user.getUserId(),
                    train.getStations().get(0),
                    train.getStations().get(train.getStations().size() - 1),
                    LocalDate.now().toString(),
                    train
            );

            if (user.getTicketsBooked() == null) {
                user.setTicketsBooked(new ArrayList<>());
            }
            user.getTicketsBooked().add(ticket);


            for (int i = 0; i < userList.size(); i++) {
                if (Objects.equals(userList.get(i).getUserId(), user.getUserId())) {
                    userList.set(i, user);
                    break;
                }
            }

            saveUserListToFile();
            return true;

        } catch (Exception ex) {
            System.out.println("Something went wrong while booking seat.");
            ex.printStackTrace();
            return false;
        }
    }



    public Boolean cancelBooking(String ticketId) {
        if (user == null) {
            System.out.println("Please login first.");
            return Boolean.FALSE;
        }

        boolean removed = user.getTicketsBooked()
                .removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (removed) {
            try {
                saveUserListToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        } else {
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }
}