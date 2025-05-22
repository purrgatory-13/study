package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.InputManager;
import org.example.managers.OutputManager;
import org.example.models.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * Команда update id {element}: обновить значение элемента коллекции, id которого равен заданному
 */
public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;
    private final InputManager inputManager;
    private final OutputManager outputManager;

    public UpdateCommand(CollectionManager collectionManager,
                         InputManager inputManager,
                         OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.inputManager = inputManager;
        this.outputManager = outputManager;
    }

    @Override
    public void execute(String args) {
        if (args.isEmpty()) {
            outputManager.printError("Необходимо указать id элемента для обновления.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args.trim());
        } catch (NumberFormatException e) {
            outputManager.printError("ID должен быть числом.");
            return;
        }

        Optional<Ticket> optionalTicket = collectionManager.getById(id);
        if (optionalTicket.isEmpty()) {
            outputManager.printError("Элемент с ID " + id + " не найден в коллекции.");
            return;
        }

        outputManager.println("Обновление билета с ID: " + id);
        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(id); // сохраняем старый ID

        // ввод новых полей
        setName(updatedTicket);
        setCoordinates(updatedTicket);
        setPrice(updatedTicket);
        setComment(updatedTicket);
        setRefundable(updatedTicket);
        setTicketType(updatedTicket);
        setVenue(updatedTicket);

        boolean success = collectionManager.updateById(id, updatedTicket);
        if (success) {
            outputManager.println("Билет успешно обновлён.");
        } else {
            outputManager.printError("Не удалось обновить билет.");
        }
    }

    private void setName(Ticket ticket) {
        while (true) {
            String name = inputManager.readWithPrompt("Введите название билета: ");
            if (name != null && !name.trim().isEmpty()) {
                ticket.setName(name.trim());
                break;
            }
            outputManager.printError("Название не может быть пустым");
        }
    }

    private void setCoordinates(Ticket ticket) {
        Coordinates coordinates = new Coordinates();

        // Координата X
        while (true) {
            try {
                String xInput = inputManager.readWithPrompt("Введите координату X (число, ≤ 709): ");
                Double x = Double.parseDouble(xInput);
                if (x <= 709) {
                    coordinates.setX(x);
                    break;
                }
                outputManager.printError("Координата X должна быть ≤ 709");
            } catch (NumberFormatException e) {
                outputManager.printError("Некорректное число для координаты X");
            }
        }

        // Координата Y
        while (true) {
            try {
                String yInput = inputManager.readWithPrompt("Введите координату Y (целое число > -4): ");
                long y = Long.parseLong(yInput);
                if (y > -4) {
                    coordinates.setY(y);
                    break;
                }
                outputManager.printError("Координата Y должна быть > -4");
            } catch (NumberFormatException e) {
                outputManager.printError("Некорректное число для координаты Y");
            }
        }

        ticket.setCoordinates(coordinates);
    }

    private void setPrice(Ticket ticket) {
        while (true) {
            try {
                String priceInput = inputManager.readWithPrompt("Введите цену билета (> 0): ");
                int price = Integer.parseInt(priceInput);
                if (price > 0) {
                    ticket.setPrice(price);
                    break;
                }
                outputManager.printError("Цена должна быть > 0");
            } catch (NumberFormatException e) {
                outputManager.printError("Некорректное число для цены");
            }
        }
    }

    private void setComment(Ticket ticket) {
        while (true) {
            String comment = inputManager.readWithPrompt("Введите комментарий: ");
            if (comment != null && !comment.trim().isEmpty()) {
                ticket.setComment(comment.trim());
                break;
            }
            outputManager.printError("Комментарий не может быть пустым");
        }
    }

    private void setRefundable(Ticket ticket) {
        String refundableInput = inputManager.readWithPrompt("Возвратный билет? (y/n, Enter - пропустить): ");
        if (refundableInput.equalsIgnoreCase("y")) {
            ticket.setRefundable(true);
        } else if (refundableInput.equalsIgnoreCase("n")) {
            ticket.setRefundable(false);
        }
    }

    private void setTicketType(Ticket ticket) {
        StringBuilder typesInfo = new StringBuilder("Доступные типы билетов:\n");
        for (TicketType type : TicketType.values()) {
            typesInfo.append("  ").append(type.name()).append(" (").append(type.getIndex()).append(")\n");
        }
        outputManager.println(typesInfo.toString());
        while (true) {
            String typeInput = inputManager.readWithPrompt("Введите тип билета: ");
// Пробуем сначала распознать ввод как индекс
            try {
                int index = Integer.parseInt(typeInput);
                TicketType type = TicketType.getByIndex(index);
                ticket.setType(type);
                break;
            } catch (NumberFormatException e) {
                // Если не число, пробуем распознать как название
                try {
                    TicketType type = TicketType.valueOf(typeInput.toUpperCase());
                    ticket.setType(type);
                    break;
                } catch (IllegalArgumentException ex) {
                    outputManager.printError("Некорректный тип билета. Введите название или индекс.");
                }
            } catch (IllegalArgumentException e) {
                outputManager.printError("Некорректный тип билета. Введите название или индекс.");
            }
        }

    }

    private void setVenue(Ticket ticket) {
        Venue venue = new Venue();
        outputManager.println("Введите данные о месте проведения:");

        while (true) {
            String name = inputManager.readWithPrompt("  Название места: ");
            if (name != null && !name.trim().isEmpty()) {
                venue.setName(name.trim());
                break;
            }
            outputManager.printError("Название не может быть пустым");
        }

        while (true) {
            try {
                String capacityInput = inputManager.readWithPrompt("  Вместимость (> 0): ");
                int capacity = Integer.parseInt(capacityInput);
                if (capacity > 0) {
                    venue.setCapacity(capacity);
                    break;
                }
                outputManager.printError("Вместимость должна быть > 0");
            } catch (NumberFormatException e) {
                outputManager.printError("Некорректное число для вместимости");
            }
        }


        StringBuilder venueTypesInfo = new StringBuilder("Доступные типы мест:\n");
        for (VenueType type : VenueType.values()) {
            venueTypesInfo.append("  ").append(type.name()).append(" (").append(type.getIndex()).append(")\n");
        }
        outputManager.println(venueTypesInfo.toString());

        String typeInput = inputManager.readWithPrompt("  Введите тип места (Enter - пропустить): ");
        if (!typeInput.trim().isEmpty()) {
//            try {
//                VenueType type = VenueType.valueOf(typeInput.toUpperCase());
//                venue.setType(type);
//            } catch (IllegalArgumentException e) {
//                outputManager.printError("Некорректный тип места, будет пропущен");
//            }
//        }

            try {
                int index = Integer.parseInt(typeInput);
                VenueType type = VenueType.getByIndex(index);
                venue.setType(type);

            } catch (NumberFormatException e) {
                // Если не число, пробуем распознать как название
                try {
                    VenueType type = VenueType.valueOf(typeInput.toUpperCase());
                    venue.setType(type);

                } catch (IllegalArgumentException ex) {
                    outputManager.printError("Некорректный тип места, будет пропущен");
                }
            } catch (IllegalArgumentException e) {
                outputManager.printError("Некорректный тип места, будет пропущен");
            }

        ticket.setVenue(venue);
    }
} }
