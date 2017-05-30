package myapp.presentationmodel.person;

/**
 * @author Dieter Holz
 *
 * todo: specify all commands you need in your corresponding controller
 */
public interface PersonCommands {
	String SHOW_NEXT = unique("showNext");
	String SHOW_LAST = unique("showLast");
	String SAVE      = unique("save");
	String RESET     = unique("reset");

    String ON_PUSH = unique("onPush");
    String ON_RELEASE = unique(("onRelease"));

    static String unique(String key) {
		return PersonCommands.class.getName() + "." + key;
	}

}
