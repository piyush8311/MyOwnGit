package myowngit;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.action.StoreTrueArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class CommandLineArgumentsParser {
    private static Subparsers subparsers;

    public static Namespace parse(String[] args)  {

        ArgumentParser parser = ArgumentParsers.newFor("myowngit").build()
                .description("process my own git commands");
        subparsers = parser.addSubparsers().title("Commands").dest("command");

        addCatFileSubParser(); // cat-file
        addCheckoutSubParser(); // checkout
        addCommitSubParser(); // commit
        addHashObjectSubParser(); // hash-object
        addInitSubparser(); // init
        addLsTreeSubParser(); // ls-tree

        try{
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            throw new RuntimeException("Error while parsing command line args " + args, e);
        }
    }

    private static void addInitSubparser() {
        Subparser initParser = subparsers.addParser("init").help("init help");
        initParser.addArgument("path")
                .metavar("directory")
                .nargs("?")
                .setDefault(".").help("Where to create the repository");
    }

    private static void addHashObjectSubParser() {
        Subparser hashObjectParser = subparsers.addParser("hash-object")
                .help("Compute object ID and optionally creates a blob from a file");
        hashObjectParser.addArgument("-t")
                .metavar("type")
                .dest("type")
                .choices("blob", "commit", "tag", "tree")
                .setDefault("blob")
                .help("Specify the type");

        hashObjectParser.addArgument("-w")
                .dest("write")
                .action(new StoreTrueArgumentAction())
                .help("Actually write the object into the database");

        hashObjectParser.addArgument("path").help("Read object from <file>");
    }

    private static void addCatFileSubParser() {
        Subparser catFileParser = subparsers.addParser("cat-file")
                .help("Provide content of repository objects");

        catFileParser.addArgument("type")
                .metavar("type")
                .choices("blob", "commit", "tag", "tree")
                .help("Specify the type");

        catFileParser.addArgument("object")
                .metavar("object")
                .help("The object to display");
    }

    private static void addLsTreeSubParser() {
        Subparser lsTreeParser = subparsers.addParser("ls-tree");

        lsTreeParser.addArgument("object")
                .help("The object to show");
    }

    private static void addCommitSubParser() {
        Subparser commitParser = subparsers.addParser("commit");

        commitParser.addArgument("-m")
                .metavar("message")
                .dest("message")
                .help("commit with message");
    }

    private static void addCheckoutSubParser() {
        Subparser checkoutParser = subparsers.addParser("checkout");

        checkoutParser.addArgument("commit")
                .help("The commit or tree to checkout.");
        checkoutParser.addArgument("path")
                .help("The EMPTY directory to checkout on");
    }
}
