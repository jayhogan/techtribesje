package je.techtribes.component.event;

import com.structurizr.annotation.UsesContainer;
import je.techtribes.util.AbstractComponent;
import je.techtribes.component.contentsource.ContentSourceComponent;
import je.techtribes.domain.ContentSource;
import je.techtribes.domain.Event;
import je.techtribes.component.contentsource.ContentItemFilter;
import je.techtribes.util.JdbcDatabaseConfiguration;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@UsesContainer(name="Relational Database", description = "Reads from and writes to")
class EventComponentImpl extends AbstractComponent implements EventComponent {

    private JdbcEventDao eventDao;
    private ContentSourceComponent contentSourceComponent;

    EventComponentImpl(JdbcDatabaseConfiguration jdbcDatabaseConfiguration, ContentSourceComponent contentSourceComponent) {
        this.eventDao = new JdbcEventDao(jdbcDatabaseConfiguration);
        this.contentSourceComponent = contentSourceComponent;
    }

    @Override
    public List<Event> getFutureEvents(int pageSize) {
        try {
            List<Event> events = eventDao.getFutureEvents(pageSize);
            filterAndEnrich(events);

            return events;
        } catch (Exception e) {
            EventException ee = new EventException("Error getting future events", e);
            logError(ee);
            throw ee;
        }
    }

    @Override
    public List<Event> getEventsByYear(int year) {
        try {
            List<Event> events = eventDao.getEventsByYear(year);
            filterAndEnrich(events);

            return events;
        } catch (Exception e) {
            EventException ee = new EventException("Error getting events for year " + year, e);
            logError(ee);
            throw ee;
        }
    }

    @Override
    public List<Event> getEventsByMonth(int year, int month) {
        try {
            List<Event> events = eventDao.getEventsByMonth(year, month);
            filterAndEnrich(events);

            return events;
        } catch (Exception e) {
            EventException ee = new EventException("Error getting events for month " + year + "/" + month, e);
            logError(ee);
            throw ee;
        }
    }

    @Override
    public List<Event> getEvents(ContentSource contentSource, int pageSize) {
        try {
            List<Event> events = eventDao.getEvents(contentSource, pageSize);
            filterAndEnrich(events);

            return events;
        } catch (Exception e) {
            EventException ee = new EventException("Error getting events for content source with ID " + contentSource, e);
            logError(ee);
            throw ee;
        }
    }

    @Override
    public Event getEvent(int id) {
        try {
            List<Event> events = new LinkedList<>();
            Event event = eventDao.getEvent(id);
            events.add(event);
            filterAndEnrich(events);

            return event;
        } catch (Exception e) {
            EventException ee = new EventException("Error getting event with ID " + id, e);
            logError(ee);
            throw ee;
        }
    }

    @Override
    public long getNumberOfEvents(ContentSource contentSource, Date start, Date end) {
        try {
            return eventDao.getNumberOfEvents(contentSource, start, end);
        } catch (Exception e) {
            EventException ee = new EventException("Error getting number of events for content source with ID " + contentSource.getId() + " between " + start + " and " + end, e);
            logError(ee);
            throw ee;
        }
    }

    private void filterAndEnrich(List<Event> events) {
        ContentItemFilter<Event> filter = new ContentItemFilter<>();
        filter.filter(events, contentSourceComponent, true);
    }

}