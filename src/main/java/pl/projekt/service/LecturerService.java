package pl.projekt.service;
import pl.projekt.models.Lecturer;
import pl.projekt.repository.LecturerRepository;

/**
 * @brief Class Responsible for Lecturer logic, directly
 * communicates with LecturerRepository.
 */
public class LecturerService{

    private final LecturerRepository repository;

    /**
     * @brief Constructor initializes repository.
     */
    public LecturerService() {
        this.repository = new LecturerRepository();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public LecturerService(LecturerRepository repository) {
        this.repository = repository;
    }

    /**
     * @brief Function checks whether lecturer's database is empty or not.
     *
     * @return True if empty false otherwise.
     */
    public boolean isEmpty(){
        return repository.isEmpty();
    }

    /**
     * @brief Function adds Lecturer to the database.
     *
     * @param lecturer Given lecturer.
     * @return True if added false otherwise.
     */
    public boolean addLecturer(Lecturer lecturer){
        lecturer.setPasswordHash(AuthenticationService.passwordToHash(lecturer.getPasswordHash()));

        return repository.addLecturer(lecturer);
    }

    /**
     * @brief Lecturer getter.
     *
     * @return Lecturer from the database.
     */
    public Lecturer getLecturer(){
        return repository.getLecturer();
    }
}