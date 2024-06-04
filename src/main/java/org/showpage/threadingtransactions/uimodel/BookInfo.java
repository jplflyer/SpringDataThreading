package org.showpage.threadingtransactions.uimodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInfo {
    private int requestId;
    private boolean isDone;
    private String data;
}
