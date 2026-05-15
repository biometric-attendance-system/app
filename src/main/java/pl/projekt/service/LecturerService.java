package pl.projekt.service;
import pl.projekt.models.Lecturer;
import pl.projekt.repository.LecturerRepository;

public class LecturerService{
    
    private final LecturerRepository repository = new LecturerRepository();
    
    public boolean isEmpty(){
        return repository.isEmpty();
    }

    public boolean addLecturer(Lecturer lecturer){
        lecturer.setPinHash(AuthenticationService.pinToHash(lecturer.getPinHash()));

        return repository.addLecturer(lecturer);
    }

    public Lecturer getLecturer(){
        return repository.getLecturer();
    }

    public boolean setLecturer(Lecturer lecturer){
        lecturer.setPinHash(AuthenticationService.pinToHash(lecturer.getPinHash()));
        return repository.setLecturer(lecturer);
    }

}