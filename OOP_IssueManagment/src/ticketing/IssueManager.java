package ticketing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ticketing.Ticket.Severity;

public class IssueManager {

    private Map<String, User> users = new TreeMap<>();
    private Map<String, Component> components = new TreeMap<>();
    private Map<Integer, Ticket> tickets = new TreeMap<>();

    /***
     * Il metodo createUser() riceve uno username e il set dei ruoli
     * (UserClasses) che l'utente svolge. In alternativa al set si può usare una
     * lista variabile di argomenti. In entrambi i casi il metodo lancia
     * un'eccezione se lo username è già stato inserito o se nessun ruolo è
     * indicato.
     * 
     * @throws TicketException
     * 
     */

    public void createUser(String username, Set<UserClass> roles) throws TicketException {
	if (users == null)
	    throw new TicketException();
	if (users.size() == 0)
	    throw new TicketException();
	if (users.containsKey(username))
	    throw new TicketException();
	users.put(username, new User(username, roles));
    }

    public void createUser(String username, UserClass role) throws TicketException {
	Set<UserClass> su = new HashSet<>();
	su.add(role);
	createUser(username, su);
    }

    /**
     * 
     * Dato uno username si può ottenere il set dei ruoli dell'utente
     * corrispondente con il metodo getUserClasses().
     */

    public Set<UserClass> getUserClasses(String username) {
	return users.get(username).getRoles();
    }

    /***
     * I componenti forniti dall'azienda sono costituiti ricorsivamente da
     * sotto-componenti. Il metodo defineComponent() genera un nuovo componente
     * dato il nome e lancia un'eccezione se esiste già un componente con quel
     * nome.
     * 
     * @throws TicketException
     */

    public void defineComponent(String name) throws TicketException {
	if (components.containsKey(name))
	    throw new TicketException();
	components.put(name, new Component(name, null, null));
    }

    /**
     * Il metodo defineSubComponent() genera un nuovo sottocomponente dati il
     * nome e il path che identifica il predecessore (componente o
     * sotto-componente) di cui il nuovo elemento diventa sotto-componente.
     * Lancia un'eccezione se il predecessore non esiste o se ha già un
     * sotto-componente con lo stesso nome. Esempio: dato il sistema in figura,
     * per aggiungere SubC al componente System si scrive:
     * tm.defineSubComponent("SubC","/System");, mentre per aggiungere SubB.2 si
     * scrive: tm.defineSubComponent("SubB.2","/System/SubB");.
     * 
     * @throws TicketException
     * 
     **/

    public void defineSubComponent(String name, String subPath) throws TicketException {
	Component par = getComponentFromPath(subPath);
	par.addSubcomponent(name);
    }

    /**
     * Dato il path di un elemento (componente o sotto-componente) si può
     * ottenere il set dei nomi dei sotto-componenti con il metodo
     * getSubComponents("/System")
     * 
     * @throws TicketException
     * 
     */
    public Set<Component> getSubComponents(String subPath) throws TicketException {
	Component par = getComponentFromPath(subPath);
	return new HashSet<>(par.getSubcomponents().values());
    }

    /***
     * e il nome del predecessore con il metodo getParentComponent() (che dà
     * null se l'elemento non ha un predecessore).
     * 
     * @throws TicketException
     **/
    public String getParentComponent(String subPath) throws TicketException {
	Component par = getComponentFromPath(subPath);
	return par.getParent().getName();
    }

    /***
     * Un ticket è aperto con il metodo openTicket() che riceve lo username
     * dell'utente, il path dell'elemento difettoso, la descrizione
     * dell'anomalia e la severità (Severity) della stessa. Il metodo dà un id
     * univoco (intero progressivo a partire da 1) per il ticket. Lancia
     * un'eccezione se lo username non è valido, il path non identifica alcun
     * elemento, o se l'utente non svolge il ruolo Reporter.
     * 
     * @throws TicketException
     * 
     **/
    public int openTicket(String userName, String subPath, String description, Severity severity)
	    throws TicketException {
	Component par = getComponentFromPath(subPath);
	if (!users.containsKey(userName))
	    throw new TicketException();
	User user = users.get(userName);
	if (!user.getRoles().contains(UserClass.Reporter))
	    throw new TicketException();
	Ticket tic = new Ticket(user, par, description, severity);
	tickets.put(tic.getNumber(), tic);
	return tic.getNumber();
    }

    /***
     * 
     * Il metodo getTicket() riceve un id e dà l'oggetto Ticket corrispondente
     * oppure null se l'id non è valido.
     * 
     **/
    public Ticket getTicket(int id) {
	return tickets.get(id);
    }

    /**
     * Il metodo getAllTickets() dà la lista dei ticket ordinata naturalmente
     * (si legga la nota).
     ***/
    public List<Ticket> getAllTickets() {
	return new ArrayList<>(tickets.values());
    }

    /**
     * Il metodo assignTicket() riceve un ticket id e uno username: porta lo
     * stato del ticket a Assigned e collega il ticket all'utente come
     * assegnatario del ticket. Lancia un'eccezione se il ticket id o lo
     * username non sono validi, o se l'utente non svolge il ruolo Maintainer.
     * 
     * @throws TicketException
     **/

    public void assignTicket(int id, String userName) throws TicketException {
	if (!tickets.containsKey(id))
	    throw new TicketException();
	if (!users.containsKey(userName))
	    throw new TicketException();
	if (!users.get(userName).getRoles().contains(UserClass.Maintainer))
	    throw new TicketException();
	tickets.get(id).setAssignee(users.get(userName));
    }

    /**
     * Il metodo closeTicket() riceve un ticket id e la descrizione della
     * soluzione, e porta lo stato del ticket a Closed. Lancia un'eccezione se
     * il ticket non si trova nello stato Assigned.
     * 
     * @throws TicketException
     **/
    public void closeTicket(int id, String description) throws TicketException {
	if (!tickets.containsKey(id))
	    throw new TicketException();
	if (tickets.get(id).getState() != Ticket.State.Assigned)
	    throw new TicketException();
	tickets.get(id).close(description);
    }

    /***
     * Il metodo countBySeverityOfState() dato uno stato dei ticket fornisce una
     * mappa con il numero di ticket per Severity, considerando soltanto i
     * ticket in quello stato oppure tutti i ticket se l'argomento è nullo. La
     * mappa è ordinata in base alla Severity.
     * 
     */
    public Map<Ticket.Severity, Long> countBySeverityOfState(Ticket.State stateP) {
	if (stateP != null)
	    return tickets.values().stream().filter(t -> t.getState() == stateP)
		    .collect(Collectors.groupingBy(Ticket::getSeverity, TreeMap::new, Collectors.counting()));

	return tickets.values().stream()
		.collect(Collectors.groupingBy(Ticket::getSeverity, TreeMap::new, Collectors.counting()));
    }

    /**
     * Il metodo topMaintainers() dà una lista di stringhe: ogni stringa ha il
     * formato "username:###" dove username è il nome dell'utente e ### è il
     * numero di ticket chiusi dall'utente come maintainer. La lista è ordinata
     * per numero decrescente di ticket e poi per username (in ordine
     * alfabetico).
     * 
     **/
    public List<String> topMaintainers() {
	return tickets.values().stream()
		.collect(Collectors.groupingBy(t -> t.getAssignee().getName(), Collectors.counting())).entrySet()
		.stream().map(e -> e.getKey() + ":" + String.format("%03d", e.getValue())).collect(Collectors.toList());
    }

    public Component getComponentFromPath(String subPath) throws TicketException {
	String[] path = subPath.split("/");
	Component par = components.get(path[1]);
	path = Arrays.copyOfRange(path, 2, path.length);
	for (String st : path) {
	    if (!par.getSubcomponents().containsKey(st))
		throw new TicketException();
	    par = par.getSubcomponents().get(st);
	}
	return par;
    }

    public static enum UserClass {
	Reporter, Maintainer
    }
}
