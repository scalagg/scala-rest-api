package gg.scala.restapi.webconfig;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Luna
 */
@Component
public class WebsiteConfigurationDefaults implements ApplicationRunner {
    private final ApplicationCategoryRepository applicationCategories;
    private final TicketCategoryRepository ticketCategories;

    public WebsiteConfigurationDefaults(ApplicationCategoryRepository applicationCategories, TicketCategoryRepository ticketCategories) {
        this.applicationCategories = applicationCategories;
        this.ticketCategories = ticketCategories;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (ticketCategories.count() == 0) {
            TicketCategory support = new TicketCategory();
            support.setId("support");
            support.setName("Support");
            support.setOrder(1);
            support.setSubcategories(List.of("General Help", "Account Help", "Purchase Help", "Bug Report"));
            support.setSideboxTitle("Before opening a ticket");
            support.setSideboxContent("Include the relevant username, what happened, and any useful evidence.");
            ticketCategories.save(support);
        }
        if (applicationCategories.count() == 0) {
            ApplicationCategory staff = new ApplicationCategory();
            staff.setId("staff");
            staff.setName("Staff");
            staff.setSlug("staff");
            staff.setOrder(1);
            staff.setQuestions(List.of("text || What is your Minecraft username?", "text || What is your timezone?", "textarea || Why do you want to join the staff team?", "textarea || What relevant experience do you have?"));
            applicationCategories.save(staff);

            ApplicationCategory creator = new ApplicationCategory();
            creator.setId("creator");
            creator.setName("Creator");
            creator.setSlug("creator");
            creator.setOrder(2);
            creator.setQuestions(List.of("text || What is your Minecraft username?", "text || Link your primary channel", "textarea || Tell us about your audience and content"));
            applicationCategories.save(creator);

            ApplicationCategory partner = new ApplicationCategory();
            partner.setId("partner");
            partner.setName("Partner");
            partner.setSlug("partner");
            partner.setOrder(3);
            partner.setQuestions(List.of("text || What organization do you represent?", "text || Link your website or community", "textarea || Describe the partnership you are proposing"));
            applicationCategories.save(partner);
        }
    }
}
