package Agentes;

import java.util.Random;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Invitado3 extends Agent {

	private AID[] listaInv;
	Vector<String> comida = new Vector<String>();

	protected void setup() {
		doWait(15000);
		// Registrar party-guest
		DFAgentDescription description = new DFAgentDescription();
		description.setName(getAID());
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("party-guest");
		serviceDescription.setName(getAID().getLocalName());
		description.addServices(serviceDescription);
		try {
			DFService.register(this, description);
			System.out.println("##################### "
					+ getAID().getLocalName()
					+ " ha llegado a la fiesta. #####################");
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Buscar invitados
		invitados();
		addBehaviour(new Saludar());
		comida.add("Agua");
		comida.add("Gin tonic");
		comida.add("Ibuprofeno");
		addBehaviour(new Responder());

	}

	private class Saludar extends Behaviour {

		MessageTemplate tratarRespuestasSaludo;
		boolean fin = false;

		public void onStart() {
			System.out.println("-" + getLocalName()
					+ ": Voy a saludar a todos.");
		}

		public void action() {
			for (int i = 0; i < listaInv.length; i++) {
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.addReceiver(listaInv[i]);
				cfp.setConversationId("Saludo");
				cfp.setContent("Hola");
				System.out.println("-" + getLocalName() + ": ¡Buenas noches "
						+ listaInv[i].getLocalName() + "!");
				send(cfp);
				doWait(2000);
				tratarRespuestasSaludo = MessageTemplate
						.MatchConversationId("ResponderSaludo");
				ACLMessage respuesta = myAgent.receive(tratarRespuestasSaludo);
				if (respuesta != null) {
					System.out.println("-" + getLocalName()
							+ ": ¡Muy bien! Luego hablamos "
							+ respuesta.getSender().getLocalName());
					System.out.println();
				} else {
					block();
				}
			}
			doWait(2000);

			// Saludo al anfitrion
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.addReceiver(new AID("Anfitrion", AID.ISLOCALNAME));
			cfp.setConversationId("Saludo");
			cfp.setContent("Hola");
			System.out.println("-" + getLocalName()
					+ ": ¡Buenas noches Anfitrion!");
			send(cfp);
			doWait(2000);
			tratarRespuestasSaludo = MessageTemplate
					.MatchConversationId("ResponderSaludo");
			ACLMessage respuesta = myAgent.receive(tratarRespuestasSaludo);
			if (respuesta != null) {
				System.out.println("-" + getLocalName()
						+ ": ¡Muy bien! Luego hablamos "
						+ respuesta.getSender().getLocalName());
			} else {
				block();
			}
			fin = true;
		}

		public boolean done() {
			return fin;
		}

	}

	public class Responder extends CyclicBehaviour {
		MessageTemplate tratarSaludos;
		MessageTemplate tratarComida;

		public void action() {
			tratarSaludos = MessageTemplate.MatchConversationId("Saludo");
			tratarComida = MessageTemplate.MatchConversationId("Comida");
			ACLMessage msg = myAgent.receive(tratarSaludos);
			ACLMessage msg2 = myAgent.receive(tratarComida);
			if (msg != null) {
				// Mensaje tipo Saludo
				ACLMessage reply = msg.createReply();
				reply.setConversationId("ResponderSaludo");
				reply.setContent("Saludo");
				System.out.println("-" + getLocalName()
						+ ": ¡Buenas noches. Que tal "
						+ msg.getSender().getLocalName() + "!");
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
							+ ": No gracias, ya tengo suficiente!!!");
					send(reply2);
					addBehaviour(new despedir(myAgent, 3000));
				}
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
			listaInv = new AID[result.length - 1];
			int f = 0;
			for (int i = 0; i < result.length; ++i) {
				if (result[i].getName().getLocalName().equals(
						this.getLocalName()) != true) {
					listaInv[f] = result[i].getName();
					f++;
				}
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
			MessageTemplate tratarRespuestasAdios;
			long tfin = System.currentTimeMillis();
			int nticks = getTickCount();

			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.addReceiver(new AID("Anfitrion", AID.ISLOCALNAME));
			cfp.setConversationId("Despedida");
			cfp.setContent("Adios");
			System.out.println("-" + getLocalName()
					+ ": Bueno, yo me voy. ¡Gracias por todo Anfitrion!");
			send(cfp);
			doWait(2000);
			tratarRespuestasAdios = MessageTemplate
					.MatchConversationId("ResponderAdios");
			ACLMessage respuesta = myAgent.receive(tratarRespuestasAdios);
			if (respuesta != null) {
				System.out.println("-" + getLocalName()
						+ ": ¡Lo mismo digo! ¡Hasta la próxima "
						+ respuesta.getSender().getLocalName() + "!\n");
				System.out.println("##################### "
						+ getAID().getLocalName()
						+ " ha salido de la fiesta. #####################");
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
