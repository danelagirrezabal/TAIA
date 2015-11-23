package Agentes;

import java.util.concurrent.CyclicBarrier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Anfitrion extends Agent {

	protected void setup() {
		// Registrar party-host
		DFAgentDescription description = new DFAgentDescription();
		description.setName(getAID());
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("party-host");
		serviceDescription.setName(getAID().getLocalName());
		description.addServices(serviceDescription);
		try {
			DFService.register(this, description);
			System.out.println(getAID().getLocalName()
					+ " esta preparado para recibir a todos los invitados.");
			System.out.println();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		SequentialBehaviour seqBehaviour = new SequentialBehaviour();
		seqBehaviour.addSubBehaviour(new TickerBehaviour(this, 10000) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				ACLMessage msg = blockingReceive(MessageTemplate
						.MatchPerformative(ACLMessage.INFORM));
				if (msg.getContent().equalsIgnoreCase("hola")) {
					System.out.println("(" + getLocalName() + ") Buenos dias "+ msg.getSender().getLocalName());
				}
			}
		});
		addBehaviour(seqBehaviour);
	}

	private class escuchar extends CyclicBehaviour {
		public void action() {

		}
	}

}
