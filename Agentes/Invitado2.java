package Agentes;

import java.util.Random;

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

public class Invitado2 extends Agent {

	private AID[] listaInv;

	protected void setup() {
		doWait(10000);
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
		invitados();
		// Saludar a todos
		addBehaviour(new Saludar());
		addBehaviour(new Responder());
		addBehaviour(new despedir(this, 20000));

	}

	private class Saludar extends Behaviour {

		MessageTemplate tratarRespuestasSaludo;
		boolean fin = false;

		public void onStart() {
			System.out
					.println("---------------------------------------------------------------------------------");
			System.out
					.println("["
							+ getLocalName()
							+ "] : He entrado... Vamos a saludar a los invitados que están en la sala.");
			System.out
					.println("---------------------------------------------------------------------------------\n");
		}

		public void action() {

			for (int i = 0; i < listaInv.length; i++) {
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.addReceiver(listaInv[i]);
				cfp.setConversationId("Saludo");
				cfp.setContent("Hola");
				System.out.println("[" + getLocalName()
						+ "]: Que tal va la noche "
						+ listaInv[i].getLocalName() + "!!!");
				send(cfp);
				doWait(2000);
				tratarRespuestasSaludo = MessageTemplate
						.MatchConversationId("ResponderSaludo");
				ACLMessage respuesta = myAgent.receive(tratarRespuestasSaludo);
				if (respuesta != null) {
					// if (respuesta.getPerformative() == ACLMessage.PROPOSE) {
					System.out.println("[" + getLocalName()
							+ "]: Ya hablaremos después "
							+ respuesta.getSender().getLocalName() + "!!");
					System.out.println();
					// }
				} else {
					block();
				}
			}
			if (listaInv.length == 0) {
				System.out
						.println("["
								+ getLocalName()
								+ "] : Soy el primero en llegar. ¡¡Mejor voy a saludar al anfitrión!!\n");
			}
			doWait(2000);

			// Saluda anfitrion
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.addReceiver(new AID("Anfitrion", AID.ISLOCALNAME));
			cfp.setConversationId("Saludo");
			cfp.setContent("Hola");
			System.out.println("[" + getLocalName() + "]: Que tal va la noche "
					+ "Anfitrion" + "!!!");
			send(cfp);
			doWait(2000);
			tratarRespuestasSaludo = MessageTemplate
					.MatchConversationId("ResponderSaludo");
			ACLMessage respuesta = myAgent.receive(tratarRespuestasSaludo);
			if (respuesta != null) {
				// if (respuesta.getPerformative() == ACLMessage.CFP) {
				System.out.println("[" + getLocalName()
						+ "]: Ya hablaremos después "
						+ respuesta.getSender().getLocalName() + "!!");
				System.out.println();

				// }
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
			// tratarComida = MessageTemplate.MatchConversationId("Comida");
			ACLMessage msg = myAgent.receive(tratarSaludos);
			// ACLMessage msg2 = myAgent.receive(tratarComida);
			if (msg != null) {
				// Se ha recibido un mensaje de Saludo y lo procesamos
				ACLMessage reply = msg.createReply();
				reply.setConversationId("ResponderSaludo");
				reply.setContent("Que pasa troncoo");
				// reply.setPerformative(ACLMessage.CFP);
				System.out.println("[" + getLocalName() + "]: ¡¡¡Que pasa "
						+ msg.getSender().getLocalName() + "!!!");
				send(reply);
			}
			// else if (msg2 != null) {
			// // Se ha recibido un mensaje de Comida y lo procesamos
			// ACLMessage reply2 = msg2.createReply();
			// reply2.setConversationId("ResponderComida");
			// Random rand = new Random();
			// int ranNum = rand.nextInt(10 - 1 + 1) + 1;
			// if (ranNum == 3) {
			// reply2.setContent("Gin Tonic");
			// bebida++;
			// tragos++;
			// } else if (ranNum <= 2) {
			// reply2.setContent("Cerbeza");
			// bebida++;
			// tragos++;
			// } else if (ranNum == 4) {
			// reply2.setContent("Patxaran");
			// bebida++;
			// tragos++;
			// } else if (ranNum == 5) {
			// reply2.setContent("Martini");
			// bebida++;
			// tragos++;
			// } else if (ranNum == 5) {
			// reply2.setContent("Ron");
			// bebida++;
			// tragos++;
			// } else if (ranNum == 6) {
			// reply2.setContent("Whisky");
			// bebida++;
			// tragos++;
			// } else if (ranNum == 7) {
			// reply2.setContent("Agua");
			// bebida--;
			// } else if (ranNum > 7) {
			// reply2.setContent("Comida");
			// comida++;
			// }
			// reply2.setPerformative(ACLMessage.REQUEST);
			// System.out.println("[" + getLocalName() +
			// "]: ¡¡¡Dame un poco de "
			// + reply2.getContent() + " "
			// + msg2.getSender().getLocalName() + "!!!");
			// send(reply2);
			// }
			else {
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
			// System.out.println("[" + getLocalName() + "] Invitados sala:");
			int f = 0;
			for (int i = 0; i < result.length; ++i) {
				if (result[i].getName().getLocalName().equals(
						this.getLocalName()) != true) {
					listaInv[f] = result[i].getName();
					// System.out.println(result[i].getName().getLocalName());
					f++;
				}
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// System.out.println();
	}

	private class despedir extends TickerBehaviour {

		int minticks;

		public despedir(Agent a, long intervalo) {
			super(a, intervalo);
			minticks = 0;
		}

		public void reset() {
			super.reset();
		}

		protected void onTick() {
			MessageTemplate tratarRespuestasAdios;
			long tfin = System.currentTimeMillis();
			int nticks = getTickCount();
			minticks++;

			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.addReceiver(new AID("Anfitrion", AID.ISLOCALNAME));
			cfp.setConversationId("Despedida");
			cfp.setContent("Adios");
			System.out.println("[" + getLocalName() + "]: Uff ya llevo "
					+ " tragos mejor me voy...");
			System.out.println("[" + getLocalName()
					+ "]: ¡¡¡Bueno increible fiesta, se agradece "
					+ "Anfitrion" + "!!!");
			send(cfp);
			doWait(2000);
			tratarRespuestasAdios = MessageTemplate
					.MatchConversationId("ResponderAdios");
			ACLMessage respuesta = myAgent.receive(tratarRespuestasAdios);
			if (respuesta != null) {
				System.out.println("[" + getLocalName()
						+ "]: ¡¡¡Venga hasta otra!!!! "
						+ respuesta.getSender().getLocalName() + "!!\n");
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
