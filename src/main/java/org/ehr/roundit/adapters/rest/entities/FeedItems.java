package org.ehr.roundit.adapters.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedItems {
    private FeedItemImpl[] feedItems;
}
