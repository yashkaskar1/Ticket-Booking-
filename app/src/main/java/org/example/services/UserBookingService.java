package org.example.services;

import org.example.utils.*;
import org.example.entities.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {
    private User user1;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "app/src/main/java/org/example/localdb/user.json";

    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        loadUsers();
    }

    public UserBookingService() throws IOException {
        loadUsers();
    }

    private void loadUsers() throws IOException {
        userList = objectMapper.readValue(new File(USERS_PATH), new TypeReference<List<User>>() {});
    }

    public Boolean loginUser() {
        Optional<User> founduser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName())
                    && userserviceutil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return founduser.isPresent();
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings() {
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName())
                    && userserviceutil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if (userFetched.isPresent()) {
            userFetched.get().printTickets();
        }
    }

    public Boolean cancelBooking(String ticketId) {

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId; // Because strings are immutable
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        } else {
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }

    }

    public List<Train> gettrains(String source, String destination) {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (IOException ex) {
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

}
