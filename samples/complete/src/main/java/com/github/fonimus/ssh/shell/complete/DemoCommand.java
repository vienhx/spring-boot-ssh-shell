package com.github.fonimus.ssh.shell.complete;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.shell.Availability;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;

import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.auth.SshAuthentication;
import com.github.fonimus.ssh.shell.commands.SshShellComponent;

/**
 * Demo command for example
 */
@SshShellComponent
public class DemoCommand {

	private final SshShellHelper helper;

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoCommand.class);

	public DemoCommand(SshShellHelper helper) {
		this.helper = helper;
	}

	/**
	 * Echo command
	 *
	 * @param message message to print
	 * @return message
	 */
	@ShellMethod("Echo command")
	public String echo(@ShellOption(valueProvider = CustomValuesProvider.class) String message) {
		return message;
	}

	/**
	 * Ex command
	 *
	 * @throws IllegalStateException for example
	 */
	@ShellMethod("Ex command")
	public void ex() {
		throw new IllegalStateException("Test exception message");
	}

	/**
	 * Interaction example command
	 *
	 * @return welcome message
	 */
	@ShellMethod("Welcome command")
	public String welcome() {
		helper.printInfo("You are now in the welcome command");
		String name = helper.read("What's your name ?");
		return "Hello, '" + name + "' !";
	}

	/**
	 * Confirmation example command
	 *
	 * @return welcome message
	 */
	@ShellMethod("Confirmation command")
	public String conf() {
		return helper.confirm("Are you sure ?") ? "Great ! Let's do it !" : "Such a shame ...";
	}

	/**
	 * Admin only example command
	 *
	 * @return welcome message
	 */
	@ShellMethod("Admin command")
	@ShellMethodAvailability("adminAvailability")
	public String admin() {
		return "Finally an administrator !!";
	}

	public Availability adminAvailability() {
		if (!helper.checkAuthorities(Collections.singletonList("ADMIN"))) {
			return Availability.unavailable("admin command is only for an admin users !");
		}
		return Availability.available();
	}

	/**
	 * Authentication example command
	 *
	 * @return principal
	 */
	@ShellMethod("Authentication command")
	public SshAuthentication authentication() {
		return helper.getAuthentication();
	}

	/**
	 * For scheduled command example
	 */
	@Scheduled(initialDelay = 0, fixedDelay = 60000)
	public void log() {
		LOGGER.info("In scheduled task..");
	}
}

@Component
class CustomValuesProvider
		extends ValueProviderSupport {

	private final static String[] VALUES = new String[] {
			"message1", "message2", "message3"
	};

	@Override
	public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext, String[] hints) {
		return Arrays.stream(VALUES).map(CompletionProposal::new).collect(Collectors.toList());
	}
}