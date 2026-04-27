package pl.projekt.service;
import pl.projekt.models.Lecturer;
import pl.projekt.repository.LecturerRepository;

public class LecturerService{
    private final LecturerRepository repository;

    public LecturerService(LecturerRepository repository){
        this.repository = repository;
    }

    public boolean deleteLecturer(String ID){
        return repository.deleteLecturer(ID);
    }

    public String getHashedPin(String ID){
        Lecturer lecturer = repository.getLecturer(ID);
        if (lecturer != null) {
            return lecturer.getPinHash();
        }
        
        return null;
    }

    public boolean addLecturer(Lecturer lecturer){
        lecturer.setPinHash(AuthenticationService.pinToHash(lecturer.getPinHash()));

        return repository.addLecturer(lecturer);
    }

    public Lecturer getLecturer(String ID){
        return repository.getLecturer(ID);
    }

    public boolean setLecturer(Lecturer lecturer){
        lecturer.setPinHash(AuthenticationService.pinToHash(lecturer.getPinHash()));
        return repository.setLecturer(lecturer);
    }

}