package Agentes;

import java.util.Random;
import java.util.Vector;
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
	Vector<String> comida =  new Vector<String>();
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
					+ " esta preparado para su fiesta.");
			System.out.println();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		comida.add("Agua");
		comida.add("Vino");
		comida.add("Gin Tonic");
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
				reply.setContent("Saludo");
				System.out.println("-" + getLocalName() + ": ¡¡¡Que tal "
						+ msg.getSender().getLocalName() + ". Cuanto tiempo!!!");
				send(reply);

			} else if (msg2 != null) {
				// Se ha recibido un mensaje de Comida/Bebida y lo procesamos
				ACLMessage reply2 = msg2.createReply();
				reply2.setConversationId("ResponderComida");
				Random rand = new Random();
				int ranNum = rand.nextInt(2 - 0 + 1) + 0;
				//reply2.setContent(comida.elementAt(ranNum));
				//comida.remove(comida.elementAt(ranNum));
				if (comida.size()!=0){
					reply2.setContent(comida.elementAt(0));
					comida.remove(comida.elementAt(0));
					reply2.setPerformative(ACLMessage.REQUEST);
					System.out.println("[" + getLocalName()
						+ "]: ï¿½ï¿½ï¿½Dame un poco de " + reply2.getContent() + " "
						+ msg2.getSender().getLocalName() + "!!!");
					send(reply2);	
				} else{
					reply2.setContent("Suficiente");
					reply2.setPerformative(ACLMessage.REQUEST);
					System.out.println("[" + getLocalName()
							+ "]: No gracias, ya tengo suficiente!!!");
					send(reply2);	
				}
			} else if (msg3 != null) {
				// Se ha recibido un mensaje de Adios y lo procesamos
				ACLMessage reply = msg3.createReply();
				reply.setConversationId("ResponderAdios");
				reply.setContent("Adios");
				// reply.setPerformative(ACLMessage.CFP);
				System.out.println("[" + getLocalName()
						+ "]: ï¿½ï¿½ï¿½Muchas gracias por venir!!! ï¿½ï¿½ï¿½Hasta otra "
						+ msg3.getSender().getLocalName() + "!!!");
				send(reply);
			} else {
				block();
			}
		}

	}

}
