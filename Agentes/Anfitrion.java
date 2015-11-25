package Agentes;

import java.util.Random;
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
		addBehaviour(new Escuchar());
	}

	private class Escuchar extends CyclicBehaviour {
		MessageTemplate tratarSaludos;
		MessageTemplate tratarComida;
		MessageTemplate tratarAdios;

		public void action() {
			tratarSaludos = MessageTemplate.MatchConversationId("Saludo");
			tratarComida = MessageTemplate.MatchConversationId("Comida");
			tratarAdios = MessageTemplate.MatchConversationId("Despedida");
			ACLMessage msg = myAgent.receive(tratarSaludos);
			ACLMessage msg2 = myAgent.receive(tratarComida);
			ACLMessage msg3 = myAgent.receive(tratarAdios);
			if (msg != null) {
				// Se ha recibido un mensaje de Saludo y lo procesamos
				ACLMessage reply = msg.createReply();
				reply.setConversationId("ResponderSaludo");
				reply.setContent("Que pasa troncoo");
				// reply.setPerformative(ACLMessage.CFP);
				System.out.println("[" + getLocalName() + "]: ¡¡¡Que pasa "
						+ msg.getSender().getLocalName() + "!!!");
				send(reply);

			} else if (msg2 != null) {
				// Se ha recibido un mensaje de Comida/Bebida y lo procesamos
				ACLMessage reply2 = msg2.createReply();
				reply2.setConversationId("ResponderComida");
				Random rand = new Random();
				int ranNum = rand.nextInt(10 - 1 + 1) + 1;
				if (ranNum == 3) {
					reply2.setContent("Gin Tonic");
//					bebida++;
//					tragos++;
				} else if (ranNum <= 2) {
					reply2.setContent("Cerbeza");
//					bebida++;
//					tragos++;
				} else if (ranNum == 4) {
					reply2.setContent("Patxaran");
//					bebida++;
//					tragos++;
				} else if (ranNum == 5) {
					reply2.setContent("Martini");
//					bebida++;
//					tragos++;
				} else if (ranNum == 5) {
					reply2.setContent("Ron");
//					bebida++;
//					tragos++;
				} else if (ranNum == 6) {
					reply2.setContent("Whisky");
//					bebida++;
//					tragos++;
				} else if (ranNum == 7) {
					reply2.setContent("Agua");
//					bebida--;
				} else if (ranNum > 7) {
					reply2.setContent("Comida");
//					comida++;
				}
				reply2.setPerformative(ACLMessage.REQUEST);
				System.out.println("[" + getLocalName()
						+ "]: ¡¡¡Dame un poco de " + reply2.getContent() + " "
						+ msg2.getSender().getLocalName() + "!!!");
				send(reply2);
			} else if (msg3 != null) {
				// Se ha recibido un mensaje de Adios y lo procesamos
				ACLMessage reply = msg3.createReply();
				reply.setConversationId("ResponderAdios");
				reply.setContent("Adios");
				// reply.setPerformative(ACLMessage.CFP);
				System.out.println("[" + getLocalName()
						+ "]: ¡¡¡Muchas gracias por venir!!! ¡¡¡Hasta otra "
						+ msg3.getSender().getLocalName() + "!!!");
				send(reply);
			} else {
				block();
			}
		}

	}

}
