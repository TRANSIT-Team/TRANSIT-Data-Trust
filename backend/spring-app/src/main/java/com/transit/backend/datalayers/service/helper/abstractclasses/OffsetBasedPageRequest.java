package com.transit.backend.datalayers.service.helper.abstractclasses;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

public class OffsetBasedPageRequest implements Pageable, Serializable {
	
	private int limit;
	
	private int offset;
	
	// Constructor could be expanded if sorting is needed
	private Sort sort;
	
	private boolean isPaged;
	
	public OffsetBasedPageRequest(int limit, int offset, boolean isPaged) {
		this.limit = limit;
		this.offset = offset;
		this.sort = Sort.unsorted();
		this.isPaged = isPaged;
		
	}
	
	public OffsetBasedPageRequest(int limit, int offset, Sort sort, boolean isPaged) {
		this.limit = limit;
		this.offset = offset;
		this.sort = sort;
		this.isPaged = isPaged;
	}
	
	@Override
	public boolean isPaged() {
		return this.isPaged;
	}
	
	@Override
	public int getPageNumber() {
		return offset / limit;
	}
	
	@Override
	public int getPageSize() {
		return limit;
	}
	
	@Override
	public long getOffset() {
		return offset;
	}
	
	@Override
	public Sort getSort() {
		return sort;
	}
	
	@Override
	public Pageable next() {
		// Typecast possible because number of entries cannot be bigger than integer (primary key is integer)
		return new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() + getPageSize()), this.isPaged);
	}
	
	@Override
	public Pageable previousOrFirst() {
		return hasPrevious() ? previous() : first();
	}
	
	public Pageable previous() {
		// The integers are positive. Subtracting does not let them become bigger than integer.
		return hasPrevious() ?
				new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() - getPageSize()), this.isPaged) : this;
	}
	
	@Override
	public Pageable first() {
		return new OffsetBasedPageRequest(getPageSize(), 0, this.isPaged);
	}
	
	@Override
	public Pageable withPage(int pageNumber) {
		return new OffsetBasedPageRequest(this.limit, pageNumber * limit, this.sort, this.isPaged);
	}
	
	@Override
	public boolean hasPrevious() {
		return offset > limit;
	}
}