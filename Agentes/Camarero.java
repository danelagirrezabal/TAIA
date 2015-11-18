package Agentes;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Camarero extends Agent {
	protected void setup() {
		// Registrar party-host
		DFAgentDescription description = new DFAgentDescription();
		description.setName(getAID());
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("camarero");
		serviceDescription.setName(getAID().getLocalName());
		description.addServices(serviceDescription);
		try {
			DFService.register(this, description);
			System.out
					.println("---------------------------------------------------------------------------------");
			System.out
					.println("["
							+ getLocalName()
							+ "] : El camarero entra en la sala... En seguida empiezo a trabajar");
			System.out
					.println("---------------------------------------------------------------------------------\n");
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

}
