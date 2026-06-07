package pl.projekt.service;
import pl.projekt.models.Lecturer;
import pl.projekt.repository.LecturerRepository;

/**
 * @brief Class Responsible for Lecturer logic, directly
 * communicates with LecturerRepository.
 */
public class LecturerService{
    
    private final LecturerRepository repository = new LecturerRepository();

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
        lecturer.setPinHash(AuthenticationService.pinToHash(lecturer.getPinHash()));

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