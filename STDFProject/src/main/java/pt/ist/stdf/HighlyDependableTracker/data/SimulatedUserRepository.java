package pt.ist.stdf.HighlyDependableTracker.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import pt.ist.stdf.HighlyDependableTracker.Model.SimulatedUser;

@Service
@Repository
public interface SimulatedUserRepository extends JpaRepository<SimulatedUser, Integer>{

	SimulatedUser findById(int id);
	

	
}
