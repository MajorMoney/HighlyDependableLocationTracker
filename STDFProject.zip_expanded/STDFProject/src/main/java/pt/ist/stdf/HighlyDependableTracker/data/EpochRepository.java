package pt.ist.stdf.HighlyDependableTracker.data;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ist.stdf.HighlyDependableTracker.Model.Epoch;

@org.springframework.stereotype.Repository
public interface EpochRepository extends JpaRepository<Epoch, Integer>{

	Epoch findById(int id);

}
