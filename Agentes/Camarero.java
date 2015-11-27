package Agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Camarero extends Agent {
	private AID[] listaInv;

	protected void setup() {
		doWait(7000);
		// Registrar party-host
		DFAgentDescription description = new DFAgentDescription();
		description.setName(getAID());
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("camarero");
		serviceDescription.setName(getAID().getLocalName());
		description.addServices(serviceDescription);
		try {
			DFService.register(this, description);
			System.out.println("##################### "
					+ getAID().getLocalName()
					+ " empieza a trabajar #####################");
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		doWait(30000);
		addBehaviour(new darDeComer(this, 3000));
	}

	private class darDeComer extends TickerBehaviour {

		public darDeComer(Agent a, long intervalo) {
			super(a, intervalo);
		}

		public void reset() {
			super.reset();
		}

		public void onTick() {
			MessageTemplate tratarRespuestasComida;
			invitados();
			System.out
					.println("###########################################################################");
			for (int i = 0; i < listaInv.length; i++) {
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				msg.addReceiver(listaInv[i]);
				msg.setConversationId("Comida");
				msg.setContent("Tipo de comida");
				System.out.println("-" + getLocalName() + ": ¿Qué desea "
						+ listaInv[i].getLocalName() + "?");
				send(msg);
				doWait(2000);
				tratarRespuestasComida = MessageTemplate
						.MatchConversationId("ResponderComida");
				ACLMessage respuesta = myAgent.receive(tratarRespuestasComida);
				if (respuesta != null) {
					if (respuesta.getContent().compareTo("Suficiente") != 0) {
						System.out.println("-" + getLocalName() + ": Tome su "
								+ respuesta.getContent() + " "
								+ respuesta.getSender().getLocalName() + ".");
						System.out.println();
					} else {
						System.out.println("-" + getLocalName()
								+ ":Vale perfecto, pasaré más tarde.");
						System.out.println();
					}
				} else {
					block();
				}
			}
			doWait(2000);
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.addReceiver(new AID("Anfitrion", AID.ISLOCALNAME));
			msg.setConversationId("Comida");
			msg.setContent("Tipo de comida");
			System.out
					.println("-" + getLocalName() + ": ¿Qué desea Anfitrion?");
			send(msg);
			doWait(2000);
			tratarRespuestasComida = MessageTemplate
					.MatchConversationId("ResponderComida");
			ACLMessage respuesta = myAgent.receive(tratarRespuestasComida);
			if (respuesta != null) {
				if (respuesta.getContent().compareTo("Suficiente") != 0) {
					System.out.println("-" + getLocalName()
							+ ": Tome su "
							+ respuesta.getContent() + " "
							+ respuesta.getSender().getLocalName() + ".");
					System.out.println();
				} else {
					System.out.println("-" + getLocalName()
							+ ": Vale perfecto, pasaré más tarde.");
					System.out.println();
				}

			} else {
				System.out
						.println("-"
								+ getLocalName()
								+ ": ¡Pero a dónde se ha ido el Anfitrion! Pues yo tambien me voy a casa.\n");
				try {
					DFService.deregister(myAgent);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				doDelete();
			}
			reset();

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

}
