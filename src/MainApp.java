import model.Topic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class MainApp {
    static void pressEnterKey(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n========================Press Enter to continue========================");
        scanner.nextLine();
    }
    private static JdbcImpl jdbc;
    private static Scanner scanner;
    public static void main(String[] args) {
        jdbc = new JdbcImpl();
        scanner = new Scanner(System.in);

        int choice;

        do {
            System.out.println("\n=================== Welcome to my Topic Management System ===================\n");
            System.out.println("1. Insert a new topic");
            System.out.println("2. Update an existing topic by ID");
            System.out.println("3. Delete a topic by ID");
            System.out.println("4. Select a topic by ID");
            System.out.println("5. Select a topic by name");
            System.out.println("6. Select all topic");
            System.out.println("7. Exit");

            System.out.print("==> Choose an option from (1-7) : ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    insertTopic();
                    pressEnterKey();
                    break;
                case 2:
                    updateTopic();
                    pressEnterKey();
                    break;
                case 3:
                    deleteTopic();
                    pressEnterKey();
                    break;
                case 4:
                    selectTopicById();
                    pressEnterKey();
                    break;
                case 5:
                    selectTopicByName();
                    pressEnterKey();
                    break;
                case 6:
                    selectTopic();
                    pressEnterKey();
                    break;
                case 7:
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 7);

    }

    private static void insertTopic() {
        Topic topic = new Topic();
        System.out.println("=================== INSERT TOPIC ===================");
        System.out.print("-> Enter name : ");
        topic.setName(scanner.nextLine());
        System.out.print("-> Enter description : ");
        topic.setDescription(scanner.nextLine());
        topic.setStatus(true);

        try (Connection conn = jdbc.dataSource().getConnection()) {
            String insertSql = "INSERT INTO topics (name, description, status) VALUES(?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(insertSql);
            statement.setString(1, topic.getName());
            statement.setString(2, topic.getDescription());
            statement.setBoolean(3, topic.getStatus());

            int count = statement.executeUpdate();
            System.out.println(count + " topic has inserted successfully !!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateTopic() {
        System.out.println("\n=================== UPDATE TOPIC BY ID ===================");
        System.out.print("-> Enter ID of topic you want to update : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = jdbc.dataSource().getConnection()) {
            String selectSql = "SELECT * FROM topics WHERE id = ?";
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setInt(1, id);

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                Topic topic = new Topic();
                topic.setId(id);
                System.out.print("-> Enter new name : ");
                topic.setName(scanner.nextLine());
                System.out.print("-> Enter new description : ");
                topic.setDescription(scanner.nextLine());
                topic.setStatus(true);

                String updateSql = "UPDATE topics SET name = ?, description = ?, status = ? WHERE id = ?";
                PreparedStatement updateStatement = conn.prepareStatement(updateSql);
                updateStatement.setString(1, topic.getName());
                updateStatement.setString(2, topic.getDescription());
                updateStatement.setBoolean(3, topic.getStatus());
                updateStatement.setInt(4, id);

                int count = updateStatement.executeUpdate();
                System.out.println(count + " topic has updated successfully !!");
            } else {
                System.out.println("Topic not found with id " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteTopic() {
        System.out.println("\n=================== DELETE TOPIC BY ID ===================");
        System.out.print("-> Enter the ID of the topic you want to delete : ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try (Connection conn = jdbc.dataSource().getConnection()) {
            String selectSql = "SELECT * FROM topics WHERE id = ?";
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setInt(1, id);

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String deleteSql = "DELETE FROM topics WHERE id = ?";
                PreparedStatement deleteStatement = conn.prepareStatement(deleteSql);
                deleteStatement.setInt(1, id);

                int count = deleteStatement.executeUpdate();
                System.out.println(count + " topic has deleted successfully !!");
            } else {
                System.out.println("Topic not found with id " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectTopicById() {
        System.out.println("\n=================== SELECT TOPIC BY ID ===================");
        System.out.print("-> Enter the ID of the topic you want to select : ");
        int id = scanner.nextInt();

        try (Connection conn = jdbc.dataSource().getConnection()) {
            String selectSql = "SELECT * FROM topics WHERE id = ?";
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setInt(1, id);

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                Topic topic = new Topic();
                topic.setId(id);
                topic.setName(resultSet.getString("name"));
                topic.setDescription(resultSet.getString("description"));
                topic.setStatus(resultSet.getBoolean("status"));
                System.out.println(topic);
            } else {
                System.out.println("Topic not found with id " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectTopicByName() {
        System.out.println("\n=================== SELECT TOPIC BY NAME ===================");
        System.out.print("Enter the name of the topic you want to select : ");
        String name = scanner.nextLine();

        try (Connection conn = jdbc.dataSource().getConnection()) {
//            String selectSql = "SELECT * FROM topics WHERE name = ?";
//            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
//            selectStatement.setString(1, name);
//
//            ResultSet resultSet = selectStatement.executeQuery();

            String selectSql = "SELECT * FROM topics WHERE LOWER(name) LIKE LOWER(?)";
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setString(1, "%" + name.toLowerCase() + "%");

            ResultSet resultSet = selectStatement.executeQuery();


            if (resultSet.next()) {
                Topic topic = new Topic();
                topic.setId(resultSet.getInt("id"));
                topic.setName(name);
                topic.setDescription(resultSet.getString("description"));
                topic.setStatus(resultSet.getBoolean("status"));
                System.out.println(topic);
            } else {
                System.out.println("Topic not found with name " + name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectTopic(){
        try (Connection conn = jdbc.dataSource().getConnection()){
            // 1.Create SQL Statement
            String selectSql = "SELECT * FROM topics ORDER BY id ASC";
            PreparedStatement statement= conn.prepareStatement(selectSql);

            // 2.Execute SQL Statment
            ResultSet resultSet = statement.executeQuery();

            // 3.Process Result with ResultSet
            List<Topic> topics  = new ArrayList<>();
            while (resultSet.next()){
                Integer id=resultSet.getInt("id");
                String name= resultSet.getString("name");
                String description = resultSet.getString("description");
                Boolean status = resultSet.getBoolean("status");
                topics.add(new Topic(id,name,description,status));
            }

            System.out.println("\n=================== SELECT ALL TOPIC ===================");
            for (Topic topic:topics) {
                System.out.println(topic);
            }

        } catch(Exception e) {
            e.printStackTrace();

        }
    }

}





