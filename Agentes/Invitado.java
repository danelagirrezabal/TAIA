package Agentes;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Invitado extends Agent{
	
	protected void setup(){
		// Registrar party-host
		DFAgentDescription description = new DFAgentDescription();
		description.setName(getAID());
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("party-guest");
		serviceDescription.setName(getAID().getLocalName());
		description.addServices(serviceDescription);
		try {
			DFService.register(this, description);
			 System.out.println(getAID().getLocalName() + " ha llegado.");
			 System.out.println();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}

}
