package pt.ist.stdf.Simulation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulatedUserRepository extends JpaRepository<SimulatedUser, Integer>{

	SimulatedUser findById(int id);
	

	
}
