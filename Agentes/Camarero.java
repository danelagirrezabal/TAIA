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
		addBehaviour(new OfrecerComida(this, 10000));
	}

	private class OfrecerComida extends TickerBehaviour {
		int minticks;

		public OfrecerComida(Agent a, long intervalo) {
			super(a, intervalo);
			minticks = 0;
		}

		public void reset() {
			super.reset();
		}

		public void onTick() {
			MessageTemplate tratarRespuestasComida;
			boolean fin = false;
			long tfin = System.currentTimeMillis();
			int nticks = getTickCount();
			minticks++;

			invitados();
			System.out
					.println("---------------------------------------------------------------------------------");
			System.out.println("[" + getLocalName() + "] : Ronda "
					+ " de servicio de camarero!!!!");
			System.out
					.println("---------------------------------------------------------------------------------\n");
			for (int i = 0; i < listaInv.length; i++) {
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				msg.addReceiver(listaInv[i]);
				msg.setConversationId("Comida");
				msg.setContent("Tipo de comida");
				System.out.println("[" + getLocalName()
						+ "]: ¿Quieres comer/beber algo "
						+ listaInv[i].getLocalName() + "?");
				send(msg);
				doWait(2000);
				tratarRespuestasComida = MessageTemplate
						.MatchConversationId("ResponderComida");
				ACLMessage respuesta = myAgent.receive(tratarRespuestasComida);
				if (respuesta != null) {
					// if (respuesta.getPerformative() == ACLMessage.CFP) {
					System.out
							.println("[" + getLocalName()
									+ "]: ¡¡Aqui tienes tu "
									+ respuesta.getContent() + " "
									+ respuesta.getSender().getLocalName()
									+ "!!");
					System.out.println();

					// }
				} else {
					block();
				}
			}
			doWait(2000);
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.addReceiver(new AID("Anfitrion", AID.ISLOCALNAME));
			msg.setConversationId("Comida");
			msg.setContent("Tipo de comida");
			System.out.println("[" + getLocalName()
					+ "]: ¿Quieres comer/beber algo " + "Anfitrion" + "?");
			send(msg);
			doWait(2000);
			tratarRespuestasComida = MessageTemplate
					.MatchConversationId("ResponderComida");
			ACLMessage respuesta = myAgent.receive(tratarRespuestasComida);
			if (respuesta != null) {
				// if (respuesta.getPerformative() == ACLMessage.CFP) {
				System.out.println("[" + getLocalName()
						+ "]: ¡¡Aqui tienes tu " + respuesta.getContent() + " "
						+ respuesta.getSender().getLocalName() + "!!");
				System.out.println();

				// }
			} else {
				block();
			}

			// else{
			// doWait(5000);
			// System.out.println("[" + getLocalName()
			// +
			// "]: Ya estamos como siempre, me dejan solo para recoger todo... Pues que les den, me piroooo\n");
			// try {
			// DFService.deregister(myAgent);
			// } catch (FIPAException e) {
			// e.printStackTrace();
			// }
			// doDelete();
			// }
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
			// System.out.println("[" + getLocalName() + "] Invitados sala:");
			for (int i = 0; i < result.length; ++i) {
				listaInv[i] = result[i].getName();
				// System.out.println(result[i].getName().getLocalName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// System.out.println();
	}

}
