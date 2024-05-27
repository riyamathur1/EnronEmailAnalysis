import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class A3 {
    private static String dataset;
    private static String connectorFile;

    private Map<String, Integer> sentEmailsMap;
    private Map<String, Integer> receivedEmailsMap;
    private Map<String, Integer> teamSizeMap;

    public static void main(String[] args) throws IOException {
        // Checking if the dataset path is provided as the first argument
        if (args.length >= 1) {
            dataset = args[0];
        } else {
            System.out.println("Please provide the dataset path as the first argument.");
            return;
        }
        // Check if the connector file path is provided as the second argument
        if (args.length >= 2) {
            connectorFile = args[1];    // if no args[2] is provided, connectorFile = null
        }

        A3 processor = new A3();
        processor.processDataset();
        processor.identifyConnectors();
        processor.handleUserInput();
    }

    public void processDataset() throws IOException {
        // Initializing maps to store email-related information
        sentEmailsMap = new HashMap<>();
        receivedEmailsMap = new HashMap<>();
        teamSizeMap = new HashMap<>();

        // Creating a File object for the dataset directory
        File datasetDirectory = new File(dataset);
        processDirectory(datasetDirectory);
        identifyConnectors();
    }

    private void processDirectory(File directory) throws IOException {
        // Listing all files in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // Processing subdirectories using recursion
                if (file.isDirectory()) {
                    processDirectory(file);
                // Processing individual mail documents
                } else {
                    processMailDoc(file);
                }
            }
        }
    }

    private Set<String> validEmailAddresses = new HashSet<>();

    private void processMailDoc(File file) throws IOException {
        String content = readFileContent(file);

        String sender = extractSender(content);
        String[] recipients = extractRecipients(content);

        updateValidEmailAddresses(sender, recipients);
        updateSentCount(sender);
        updateReceivedCountAndTeamSize(sender, recipients);
        countUniqueEmailAddresses(content);
    }

    private String readFileContent(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }

    private void updateValidEmailAddresses(String sender, String[] recipients) {
        Set<String> allEmails = new HashSet<>();
        allEmails.add(sender);
        allEmails.addAll(Arrays.asList(recipients));

        validEmailAddresses.addAll(allEmails);
    }

    private void updateSentCount(String sender) {
        if (validEmailAddresses.contains(sender)) {
            sentEmailsMap.put(sender, sentEmailsMap.getOrDefault(sender, 0) + 1);
        }
    }

    private void updateReceivedCountAndTeamSize(String sender, String[] recipients) {
        for (String recipient : recipients) {
            if (validEmailAddresses.contains(recipient)) {
                receivedEmailsMap.put(recipient, receivedEmailsMap.getOrDefault(recipient, 0) + 1);
                incrementTeamSize(sender);
                incrementTeamSize(recipient);
            }
        }
    }

    private void incrementTeamSize(String email) {
        teamSizeMap.put(email, teamSizeMap.getOrDefault(email, 0) + 1);
    }

    private void countUniqueEmailAddresses(String content) {
        Pattern emailPattern = Pattern.compile("(?i)((From|To|Cc|Bcc):\\s*\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,}\\b|\\b[A-Za-z0-9._%+-]+@enron\\.com\\b)");
        Matcher emailMatcher = emailPattern.matcher(content);
        Set<String> uniqueEmailAddresses = new HashSet<>();
        while (emailMatcher.find()) {
            uniqueEmailAddresses.add(emailMatcher.group());
        }
    }

    private void identifyConnectors() {
        for (String email : sentEmailsMap.keySet()) {
            int sentEmails = sentEmailsMap.get(email);
            int receivedEmails = receivedEmailsMap.getOrDefault(email, 0);
            int teamSize = teamSizeMap.getOrDefault(email, 0);
            if ((sentEmails > 0 && receivedEmails == 0) || (sentEmails == 0 && receivedEmails > 0) || (teamSize == 1)) {
                System.out.println(email);  // Print the connector to stdout
            }
        }

        if (connectorFile != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(connectorFile))) {
                for (String email : sentEmailsMap.keySet()) {
                    int sentEmails = sentEmailsMap.get(email);
                    int receivedEmails = receivedEmailsMap.getOrDefault(email, 0);
                    int teamSize = teamSizeMap.getOrDefault(email, 0);

                    if ((sentEmails > 0 && receivedEmails == 0) || (sentEmails == 0 && receivedEmails > 0) || (teamSize == 1)) {
                        writer.println(email);  // Write the connector to the file
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        String response;

        while (true) {
            System.out.print("Email address of the individual (or EXIT to quit): ");
            response = scanner.nextLine().trim();
            if (response.equalsIgnoreCase("EXIT")) {
                break;
            }

            processEmailAddress(response);
        }

        scanner.close();
    }

    private void processEmailAddress(String email) {
        if (!sentEmailsMap.containsKey(email)) {
            System.out.println("Email address (" + email + ") not found in the dataset.");
            return;
        }

        int sentEmails = sentEmailsMap.getOrDefault(email, 0);
        int receivedEmails = receivedEmailsMap.getOrDefault(email, 0);
        int teamSize = teamSizeMap.getOrDefault(email, 0);

        System.out.println("* " + email + " has sent messages to " + sentEmails + " others");
        System.out.println("* " + email + " has received messages from " + receivedEmails + " others");
        System.out.println("* " + email + " is in a team with " + teamSize + " individuals");
    }

    private String extractSender(String content) {
        // Extract the email address from the email content
        int startIndex = content.indexOf("From: ") + 6;
        int endIndex = content.indexOf("\n", startIndex);
        String sender = content.substring(startIndex, endIndex).trim();
        if (sender.toLowerCase().endsWith("@enron.com")) {
            return sender;
        }
        return "";
    }

    private String[] extractRecipients(String content) {
        int startIndex = content.indexOf("To: ") + 4;
        int endIndex = content.indexOf("\n", startIndex);
        String recipientsString = content.substring(startIndex, endIndex).trim();

        // Extract the "cc" and "bcc" recipients as well
        int ccStartIndex = content.indexOf("cc: ", endIndex);
        if (ccStartIndex != -1) {
            int ccEndIndex = content.indexOf("\n", ccStartIndex);
            recipientsString += "," + content.substring(ccStartIndex + 4, ccEndIndex).trim();
        }

        int bccStartIndex = content.indexOf("bcc: ", endIndex);
        if (bccStartIndex != -1) {
            int bccEndIndex = content.indexOf("\n", bccStartIndex);
            recipientsString += "," + content.substring(bccStartIndex + 5, bccEndIndex).trim();
        }

        String[] recipients = recipientsString.split(",");
        List<String> validRecipients = new ArrayList<>();
        for (String recipient : recipients) {
            if (recipient.toLowerCase().endsWith("@enron.com")) {
                validRecipients.add(recipient);
            }
        }
        return validRecipients.toArray(new String[0]);
    }

    public boolean isValidEmailAddress(String email) {
        // Using the regex pattern to validate the email address
        Pattern pattern = Pattern.compile("(?i)((From|To|Cc|Bcc):\\s*\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,}\\b|\\b[A-Za-z0-9._%+-]+@enron\\.com\\b)");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}








