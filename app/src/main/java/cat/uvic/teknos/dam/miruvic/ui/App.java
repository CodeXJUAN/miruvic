package cat.uvic.teknos.dam.miruvic.ui;

import cat.uvic.teknos.dam.miruvic.ui.manager.*;
import cat.uvic.teknos.dam.miruvic.repositories.*;
import cat.uvic.teknos.dam.miruvic.repositories.RepositoryFactory;
import cat.uvic.teknos.dam.miruvic.ui.manager.DiManager;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class App {

    static class RepositoryContext {
        private RepositoryFactory repositoryFactory;
        private AddressRepository addressRepository;
        private StudentRepository studentRepository;
        private RoomRepository roomRepository;
        private ReservationRepository reservationRepository;
        private PaymentRepository paymentRepository;
        private ServiceRepository serviceRepository;
        private ReservationServiceRepository reservationServiceRepository;

        public void init(DiManager diManager) {
            this.repositoryFactory = diManager.get("repository_factory");
            this.addressRepository = repositoryFactory.getAddressRepository();
            this.studentRepository = repositoryFactory.getStudentRepository();
            this.roomRepository = repositoryFactory.getRoomRepository();
            this.reservationRepository = repositoryFactory.getReservationRepository();
            this.paymentRepository = repositoryFactory.getPaymentRepository();
            this.serviceRepository = repositoryFactory.getServiceRepository();
            this.reservationServiceRepository = repositoryFactory.getReservationServiceRepository();
        }

        public void close() {
            if (repositoryFactory instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) repositoryFactory).close();
                } catch (Exception e) {
                    System.out.println("Error cerrando recursos: " + e.getMessage());
                }
            }
        }

        public AddressRepository getAddressRepository() { return addressRepository; }
        public StudentRepository getStudentRepository() { return studentRepository; }
        public RoomRepository getRoomRepository() { return roomRepository; }
        public ReservationRepository getReservationRepository() { return reservationRepository; }
        public PaymentRepository getPaymentRepository() { return paymentRepository; }
        public ServiceRepository getServiceRepository() { return serviceRepository; }
        public ReservationServiceRepository getReservationServiceRepository() { return reservationServiceRepository; }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Banner.show();

        RepositoryContext repoContext = new RepositoryContext();
        DiManager diManager;
        Properties props = new Properties();

        while (true) {
            // Selección de tecnología
            System.out.println("Seleccione la tecnología a utilizar:");
            System.out.println("1. JDBC");
            System.out.println("2. JPA");
            System.out.print("Opción: ");
            String option = scanner.nextLine();

            try {
                props.load(App.class.getResourceAsStream("/di.properties"));
                String repoFactoryClass;
                if ("1".equals(option)) {
                    repoFactoryClass = props.getProperty("jdbc_repository_factory");
                } else if ("2".equals(option)) {
                    repoFactoryClass = props.getProperty("jpa_repository_factory");
                } else {
                    System.out.println("Opción no válida. Saliendo...");
                    return;
                }
                props.setProperty("repository_factory", repoFactoryClass);
            } catch (IOException e) {
                System.out.println("Error cargando configuración: " + e.getMessage());
                return;
            }

            // Cierra recursos previos antes de cambiar de tecnología
            repoContext.close();

            diManager = new DiManager(props);
            repoContext.init(diManager);

            AddressManager addressManager = new AddressManager(repoContext.getAddressRepository(), scanner);
            StudentManager studentManager = new StudentManager(repoContext.getStudentRepository(), repoContext.getAddressRepository(), scanner);
            RoomManager roomManager = new RoomManager(repoContext.getRoomRepository(), scanner);
            ReservationManager reservationManager = new ReservationManager(repoContext.getReservationRepository(), repoContext.getStudentRepository(), repoContext.getRoomRepository(), scanner);
            PaymentManager paymentManager = new PaymentManager(repoContext.getPaymentRepository(), repoContext.getReservationRepository(), scanner);
            ServiceManager serviceManager = new ServiceManager(repoContext.getServiceRepository(), scanner);
            ReservationServiceManager reservationServiceManager = new ReservationServiceManager(repoContext.getReservationServiceRepository(), repoContext.getReservationRepository(), repoContext.getServiceRepository(), scanner);

            while (true) {
                System.out.println();
                System.out.println("=== Menú Principal ===");
                System.out.println("1. Direcciones");
                System.out.println("2. Estudiantes");
                System.out.println("3. Habitaciones");
                System.out.println("4. Reservas");
                System.out.println("5. Pagos");
                System.out.println("6. Servicios");
                System.out.println("7. Servicios de Reserva");
                System.out.println("8. Cambiar tecnología");
                System.out.println("0. Salir");
                System.out.println("Seleccione una opción: ");
                String mainOption = scanner.nextLine();
                System.out.println();

                switch (mainOption) {
                    case "1": addressManager.menu(); break;
                    case "2": studentManager.menu(); break;
                    case "3": roomManager.menu(); break;
                    case "4": reservationManager.menu(); break;
                    case "5": paymentManager.menu(); break;
                    case "6": serviceManager.menu(); break;
                    case "7": reservationServiceManager.menu(); break;
                    case "8":
                        // Cambiar tecnología: salir del bucle interno y volver a seleccionar tecnología
                        break;
                    case "0":
                        repoContext.close();
                        System.out.println("Adiós.");
                        return;
                    default: System.out.println("Opción no válida.");
                }
                if ("8".equals(mainOption)) {
                    // Cambiar tecnología
                    break;
                }
            }
        }
    }
}
