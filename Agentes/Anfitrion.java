package Agentes;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CyclicBarrier;

import jade.core.AID;
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
	Vector<String> comida = new Vector<String>();
	private AID[] listaInv;

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
			System.out.println("##################### "
					+ getAID().getLocalName()
					+ " esta preparado para su fiesta #####################");
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		comida.add("Agua");
		comida.add("Vino");
		comida.add("Gin Tonic");
		addBehaviour(new Escuchar());
		addBehaviour(new despedir(this, 10000));
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
				// Mensaje tipo Saludo
				ACLMessage reply = msg.createReply();
				reply.setConversationId("ResponderSaludo");
				reply.setContent("Saludo");
				System.out.println("-" + getLocalName() + ": ¡Que tal "
						+ msg.getSender().getLocalName() + ". Cuanto tiempo!");
				send(reply);

			} else if (msg2 != null) {
				// Mensaje tipo Comida
				ACLMessage reply2 = msg2.createReply();
				reply2.setConversationId("ResponderComida");
				if (comida.size() != 0) {
					reply2.setContent(comida.elementAt(0));
					comida.remove(comida.elementAt(0));
					reply2.setPerformative(ACLMessage.REQUEST);
					System.out.println("-" + getLocalName()
							+ ": ¿Me podrías traer " + reply2.getContent()
							+ " " + msg2.getSender().getLocalName() + "?");
					send(reply2);
				} else {
					reply2.setContent("Suficiente");
					reply2.setPerformative(ACLMessage.REQUEST);
					System.out.println("-" + getLocalName()
							+ ": No gracias, ya tengo suficiente.");
					send(reply2);
				}
			} else if (msg3 != null) {
				// Mensaje tipo Despedida
				ACLMessage reply = msg3.createReply();
				reply.setConversationId("ResponderAdios");
				reply.setContent("Adios");
				System.out
						.println("-"
								+ getLocalName()
								+ ": Estoy muy agradecido de que hayas venido. Espero que nos veamos pronto "
								+ msg3.getSender().getLocalName());
				send(reply);
			} else {
				block();
			}
		}

	}

	private void invitados() {
		DFAgentDescription agentDescription = new DFAgentDescription();
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("party-guest");
		agentDescription.addServices(serviceDescription);
		try {
			DFAgentDescription[] result = DFService.search(this,
					agentDescription);
			listaInv = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				listaInv[i] = result[i].getName();
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private class despedir extends TickerBehaviour {

		public despedir(Agent a, long intervalo) {
			super(a, intervalo);
		}

		public void reset() {
			super.reset();
		}

		protected void onTick() {
			invitados();
			if (listaInv.length == 0) {
				System.out
						.println("-" + getLocalName()
								+ ": Ha sido una velada fantastica. ¡Gracias por el trabajo realizado camarero!\n");
				System.out.println();
				try {
					DFService.deregister(myAgent);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				doDelete();
			} else {
				block();
			}
		}
	}

}
