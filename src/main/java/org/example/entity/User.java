package org.example.entity;


import java.util.List;

public class User {

    private String userId;

    private String name;

    private String password;

    private String hashedPassword;

    private List<Ticket> ticketsBooked;

    public User(String userId, String name, String password, String hashedPassword, List<Ticket> ticketsBooked) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.ticketsBooked = ticketsBooked;
    }

    public User(){}

    public String getName() {
        return name;
    }

    public String getPassword(){
        return password;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public List<Ticket> getTicketsBooked() {
        return ticketsBooked;
    }

    public void printTickets(){
        for (int i = 0; i<ticketsBooked.size(); i++){
            System.out.println(ticketsBooked.get(i).getTicketInfo());
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setTicketsBooked(List<Ticket> ticketsBooked) {
        this.ticketsBooked = ticketsBooked;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}