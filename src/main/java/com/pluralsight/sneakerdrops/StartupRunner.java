package com.pluralsight.sneakerdrops;


import com.pluralsight.sneakerdrops.data.BrandRepository;
import com.pluralsight.sneakerdrops.data.SneakerRepository;
import com.pluralsight.sneakerdrops.models.Brand;
import com.pluralsight.sneakerdrops.models.Sneaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class StartupRunner implements CommandLineRunner {
    private final SneakerRepository sneakerRepository;
    private final BrandRepository brandRepository;


    @Autowired
    private StartupRunner(SneakerRepository sneakerRepository, BrandRepository brandRepository) {
        this.sneakerRepository = sneakerRepository;
        this.brandRepository = brandRepository;

    }

    public void run(String... args) throws Exception {
        seedDate();
        for (Brand brand : brandRepository.findAll()) {
            System.out.println(brand.getId() + " - " + brand.getName());
        }
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n=== Game Library ===");
            System.out.println("1) List all sneakers");
            System.out.println("2) List sneakers by model");
            System.out.println("3) List sneakers by price");
            System.out.println("4) List sneakers by year");
            System.out.println("5) List sneakers by price and release year");
            System.out.println("6) List sneakers by id");
            System.out.println("7) Add a Sneaker");
            System.out.println("8) Update a Sneaker price");
            System.out.println("9) Delete a Sneaker");
            System.out.println("10) List sneakers by brand");
            System.out.println("0) Quit");
            System.out.print("Choose: ");
            switch (scanner.nextInt()) {
                case 1 -> listSneakers();
                case 2 -> findByModel(scanner);
                case 3 -> findByPrice(scanner);
                case 4 -> findByYear(scanner);
                case 5 -> search(scanner);
                case 6 -> viewById(scanner);
                case 7 -> addSneaker(scanner);
                case 8 -> updatePrice(scanner);
                case 9 -> deleteSneaker(scanner);
                case 10 -> listByBrand(scanner);

                case 0 -> running = false;
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void listByBrand(Scanner scanner) {
        scanner.nextLine();
        System.out.print("Brand name: ");
        String name = scanner.nextLine();
        for (Sneaker s : sneakerRepository.brandName(name)) {
            System.out.println(s.getModel());
        }
    }

    private void listAllBrands() {
        for (Brand brand : brandRepository.findAll()) {
            System.out.println(brand.getId() + " - " + brand.getName());
        }
    }

    private void addSneaker(Scanner scanner) {
        scanner.nextLine();
        System.out.print("model: ");
        String model = scanner.nextLine();
        System.out.print("price: ");
        double price = scanner.nextDouble();
        System.out.print("Release year: ");
        int year = scanner.nextInt();
        System.out.println("Choose a brand:");
        listAllBrands();
        System.out.print("Brand id: ");
        Long brandId = scanner.nextLong();
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("No brand with id " + brandId));
        sneakerRepository.save(new Sneaker(model, price, year, brand));
        System.out.println("Added!");
    }

    private void updatePrice(Scanner scanner) {
        System.out.print("Sneaker id: ");
        long id = scanner.nextLong();
        Sneaker sneaker = sneakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Sneaker with id " + id));
        System.out.print("New price: ");
        sneaker.setPrice(scanner.nextDouble());
        sneakerRepository.save(sneaker);
        System.out.println("Updated!");
    }

    private void deleteSneaker(Scanner scanner) {
        System.out.print("sneaker id: ");
        long id = scanner.nextLong();
        if (sneakerRepository.existsById(id)) {
            sneakerRepository.deleteById(id);
            System.out.println("Deleted.");
        } else {
            System.out.println("No sneaker with that id.");
        }
    }

    private void search(Scanner scanner) {
        System.out.print("Max price: ");
        double maxPrice = scanner.nextDouble();
        System.out.println("Released on or after what year: ");
        int minYear = scanner.nextInt();
        for (Sneaker s : sneakerRepository.search(maxPrice, minYear)) {
            System.out.println(s.getModel() + " (" + s.getPrice() + " " + s.getReleaseYear() + ")");
        }
    }

    private void viewById(Scanner scanner) {
        System.out.println("Sneaker id: ");
        long id = scanner.nextLong();
        Sneaker s = sneakerRepository.findById(id).orElse(null);
        if (s == null) {
            System.out.println("no sneaker found");
        } else {
            System.out.println(s.getId() + " " + s.getModel() + " (" + s.getReleaseYear() + ")");
        }
    }

    private void findByModel(Scanner scanner) {
        scanner.nextLine();
        System.out.println("Model");
        String model = scanner.nextLine();

        for (Sneaker s : sneakerRepository.findByModelContaining(model)) {
            System.out.println(s.getModel() + " (" + s.getReleaseYear() + ")");
        }
    }

    private void findByPrice(Scanner scanner) {
        System.out.println("Max Price");
        int price = scanner.nextInt();

        for (Sneaker s : sneakerRepository.findByPriceLessThan(price)) {
            System.out.println(s.getModel() + " (" + s.getReleaseYear() + ")");
        }
    }

    private void findByYear(Scanner scanner) {
        System.out.println("Year");
        int year = scanner.nextInt();

        for (Sneaker s : sneakerRepository.findByReleaseYear(year)) {
            System.out.println(s.getModel() + " (" + s.getReleaseYear() + ")");
        }
    }

    private void seedDate() {
        if (sneakerRepository.count() > 0) {
            return;
        }

        Brand nike = brandRepository.save(new Brand("Nike"));
        Brand adidas = brandRepository.save(new Brand("Adidas"));
        Brand newBalance = brandRepository.save(new Brand("New Balance"));
        Brand puma = brandRepository.save(new Brand("Puma"));
        Brand reebok = brandRepository.save(new Brand("ReeBok"));

        sneakerRepository.save(new Sneaker("nikes one", 200, 2002, nike));
        sneakerRepository.save(new Sneaker("Adidas two", 120, 2012, adidas));
        sneakerRepository.save(new Sneaker("New Balancing", 160, 2025, newBalance));
        sneakerRepository.save(new Sneaker("Puma puma", 90, 2015, puma));
        sneakerRepository.save(new Sneaker("ReeBoking", 140, 2017, reebok));

    }

    private void listSneakers() {
        System.out.println("You have " + sneakerRepository.count() + " Sneakers:");
        for (Sneaker sneaker : sneakerRepository.findAll()) {
            System.out.println(sneaker.getId() + " - " + sneaker.getModel() + " - " + sneaker.getReleaseYear() + " ($" + sneaker.getPrice() + ")");
        }

    }
}